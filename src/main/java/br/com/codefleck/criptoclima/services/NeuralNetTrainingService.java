package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.RecurrentNets;
import br.com.codefleck.criptoclima.enitities.StockDataSetIterator;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import javafx.util.Pair;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NeuralNetTrainingService {

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    ResultSetService resultSetService;
    @Autowired
    ResultService resultService;

    private static int exampleLength = 312;
    private static StockDataSetIterator iterator;

    public void trainNeuralNet(String symbol, int epochs, String selectedCategory, String period) {

        int batchSize = 32; // mini-batch size
        double splitRatio = 0.8; // 80% for training, 20% for testing
        PriceCategory category = getSelectedCategory(selectedCategory);
        String content = getCSVContent();

        System.out.println("Creating dataSet iterator...");
        iterator = new StockDataSetIterator(content, symbol, batchSize, exampleLength, splitRatio, category, period);
        System.out.println("Loading test dataset...");
        List<Pair<INDArray, INDArray>> test = iterator.getTestDataSet();

        System.out.println("Building LSTM networks...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());

        System.out.println("Training LSTM network...");
        for (int i = 0; i < epochs; i++) {
            System.out.println("epoch: " + i);
            while (iterator.hasNext()) net.fit(iterator.next()); // fit model using mini-batch data
            iterator.reset(); // reset iterator
            net.rnnClearPreviousState(); // clear previous state
        }

        //Print the  number of parameters in the network (and for each layer)
        Layer[] layers_before_saving = net.getLayers();
        int totalNumParams_before_saving = 0;
        for( int i=0; i<layers_before_saving.length; i++ ){
            int nParams = Math.toIntExact(layers_before_saving[i].numParams());
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams_before_saving += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams_before_saving);

        System.out.println("Saving model...");
        File locationToSave = new File("models/StockPriceLSTM_"
                .concat("_").concat(String.valueOf(category))
                .concat("_").concat(period.toUpperCase())
                .concat(".zip"));

        // saveUpdater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this to train your network more in the future
        try {
            ModelSerializer.writeModel(net, locationToSave, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Restoring model...");
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //print the score with every 10 iterations
        net.setListeners(new ScoreIterationListener(10));

        //Print the  number of parameters in the network (and for each layer)
        Layer[] layers = net.getLayers();
        int totalNumParams = 0;
        for( int i=0; i<layers.length; i++ ){
            int nParams = Math.toIntExact(layers[i].numParams());
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams);

        ResultSet resultSet = resultSetService.saveResultSet(new ResultSet());
        Instant instant = Instant.now();
        resultSet.setTimestamp(instant.toEpochMilli());
        resultSet.setPeriod(getTimePeriod(period));

        System.out.println("Evaluating...");
        if (category.equals(PriceCategory.ALL)) {
            INDArray max = Nd4j.create(iterator.getMaxArray());
            INDArray min = Nd4j.create(iterator.getMinArray());
            predictAllCategories(net, test, max, min, resultSet);
        } else {
            double max = iterator.getMaxNum(category);
            double min = iterator.getMinNum(category);
            predictPriceOneAhead(net, test, max, min, category);
        }
        System.out.println("Done...");
    }

    private TimePeriod getTimePeriod(String period) {
        switch (period){
            case "daily": return TimePeriod.ONE_DAY;
            case "weekly": return TimePeriod.ONE_WEEK;
        }
        return  TimePeriod.ONE_DAY;
    }

    private PriceCategory getSelectedCategory(String category) {
        switch (category){
            case "open": return PriceCategory.OPEN;
            case "close": return PriceCategory.CLOSE;
            case "low": return PriceCategory.LOW;
            case "high": return PriceCategory.HIGH;
            case "volume": return PriceCategory.VOLUME;
            case "all": return PriceCategory.ALL;
        }
        return PriceCategory.ALL;
    }

    @NotNull
    private String getCSVContent() {
        File file = new File("data/Kraken_BTCUSD_d.csv");
        System.out.println("File Found : " + file.exists());
        return file.getPath();
    }

    /** Predict one feature of a stock one-day ahead */
    public void predictPriceOneAhead (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min, PriceCategory category) {
        double[] predicts = new double[testData.size()];
        double[] actuals = new double[testData.size()];

        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getDouble(exampleLength - 1) * (max - min) + min;
            actuals[i] = testData.get(i).getValue().getDouble(0);
        }

        RegressionEvaluation eval = net.evaluateRegression(iterator);
        System.out.println(eval.stats());

        System.out.println("Printing predicted and actual values...");
        System.out.println("Predict, Actual");

        for (int i = 0; i < predicts.length; i++)
            System.out.println(predicts[i] + "," + actuals[i]);

        System.out.println("Plottig...");
    }

    /** Predict all the features (open, close, low, high prices and volume) of a stock one-day ahead */
    public ResultSet predictAllCategories (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min, ResultSet resultSet) {

        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(exampleLength - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }

        System.out.println("Printing predicted and actual values...");
        System.out.println("Predict, Actual");
        for (int i = 0; i < predicts.length; i++){
            System.out.println(predicts[i] + "\t" + actuals[i]);
        }

        if (iterator != null) {
            RegressionEvaluation eval = net.evaluateRegression(iterator);
            System.out.println(eval.stats());
        }

        List<Result> resultList = new ArrayList<>();
        for (int n = 0; n < 5; n++) {

            PriceCategory pricaCategoryForResult;
            switch (n) {
                case 0: pricaCategoryForResult = PriceCategory.OPEN; break;
                case 1: pricaCategoryForResult = PriceCategory.CLOSE; break;
                case 2: pricaCategoryForResult = PriceCategory.LOW; break;
                case 3: pricaCategoryForResult = PriceCategory.HIGH; break;
                case 4: pricaCategoryForResult = PriceCategory.VOLUME; break;
                default: throw new NoSuchElementException();
            }

            double[] pred = new double[predicts.length];
            double[] actu = new double[actuals.length];
            for (int i = 0; i < predicts.length; i++) {
                pred[i] = predicts[i].getDouble(n);
                actu[i] = actuals[i].getDouble(n);
                Result result = new Result(
                        pricaCategoryForResult,
                        Double.valueOf(predicts[i].getDouble(n)),
                        Double.valueOf(actuals[i].getDouble(n)),
                        resultSet.getPeriod(),
                        resultSet.getId()
                );
                resultList.add(resultService.saveResult(result));
            }
        }

        resultSet.setResultList(resultList);
        return resultSet;
    }
}

//example length tests with candleList.size()=1800
//360 = error
//359 = error
//358 = 1 (aparece zero no grafico, mas 1 resultado no log)
//355 = 4 (aparece zero no grafico, mas 4 no log)
//350 = 9 (aparece zero no grafico, mas 9 no log)