package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.Utils.PlotUtil;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.RecurrentNets;
import br.com.codefleck.criptoclima.enitities.StockDataSetIterator;
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
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NeuralNetTrainingService {

    @Autowired
    ResourceLoader resourceLoader;

    private static int exampleLength = 30; // time series length
    private static StockDataSetIterator iterator;

    public void trainNeuralNet(String symbol, int epochs, String selectedCategory) {

        String content = getCSVContent();

        int batchSize = 128; // mini-batch size
        double splitRatio = 0.8; // 80% for training, 20% for testing
        PriceCategory category = getSelectedCategory(selectedCategory);

        System.out.println("Creating dataSet iterator...");
        iterator = new StockDataSetIterator(content, symbol, batchSize, exampleLength, splitRatio, category);
        System.out.println("Loading test dataset...");
        List<Pair<INDArray, INDArray>> test = iterator.getTestDataSet();

        System.out.println("Building LSTM networks...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        net.init();
        net.setListeners(new ScoreIterationListener(1));

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
            int nParams = layers_before_saving[i].numParams();
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams_before_saving += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams_before_saving);

        System.out.println("Saving model...");
        File locationToSave = new File("models/StockPriceLSTM_".concat(String.valueOf(category)).concat(".zip"));

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

        //print the score with every 1 iteration
        net.setListeners(new ScoreIterationListener(1));

		//Print the  number of parameters in the network (and for each layer)
		Layer[] layers = net.getLayers();
		int totalNumParams = 0;
		for( int i=0; i<layers.length; i++ ){
			int nParams = layers[i].numParams();
			System.out.println("Number of parameters in layer " + i + ": " + nParams);
			totalNumParams += nParams;
		}
		System.out.println("Total number of network parameters: " + totalNumParams);

        System.out.println("Evaluating...");
        if (category.equals(PriceCategory.ALL)) {
            INDArray max = Nd4j.create(iterator.getMaxArray());
            INDArray min = Nd4j.create(iterator.getMinArray());
            predictAllCategories(net, test, max, min);
        } else {
            double max = iterator.getMaxNum(category);
            double min = iterator.getMinNum(category);
            predictPriceOneAhead(net, test, max, min, category);
        }
        System.out.println("Done...");
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
        File file = new File("data/coinBaseDailyCloseDez2014-Jan2018.csv");
        System.out.println("File Found : " + file.exists());

        return file.getPath();
    }

    /** Predict one feature of a stock one-day ahead */
    private static void predictPriceOneAhead (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min, PriceCategory category) {
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
        PlotUtil.plot(predicts, actuals, String.valueOf(category));
    }

    /** Predict all the features (open, close, low, high prices and volume) of a stock one-day ahead */
    private static void predictAllCategories (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min) {
        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(exampleLength - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }
        
        System.out.println("Printing predicted and actual values...");
        System.out.println("Predict, Actual");
        for (int i = 0; i < predicts.length; i++) 
        	System.out.println(predicts[i] + "\t" + actuals[i]);
        System.out.println("Plottig...");
        
        RegressionEvaluation eval = net.evaluateRegression(iterator);   
        System.out.println(eval.stats());
        
        for (int n = 0; n < 5; n++) {
            double[] pred = new double[predicts.length];
            double[] actu = new double[actuals.length];
            for (int i = 0; i < predicts.length; i++) {
                pred[i] = predicts[i].getDouble(n);
                actu[i] = actuals[i].getDouble(n);
            }
            String name;
            switch (n) {
                case 0: name = "Stock OPEN Price"; break;
                case 1: name = "Stock CLOSE Price"; break;
                case 2: name = "Stock LOW Price"; break;
                case 3: name = "Stock HIGH Price"; break;
                case 4: name = "Stock VOLUME Amount"; break;
                default: throw new NoSuchElementException();
            }
            PlotUtil.plot(pred, actu, name);
        }
    }
}
