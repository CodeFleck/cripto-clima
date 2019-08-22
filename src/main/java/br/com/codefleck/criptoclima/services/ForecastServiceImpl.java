package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.ForecastJobsDataSetIterator;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ForecastServiceImpl {

    @Autowired
    ResultService resultService;
    @Autowired
    ResultSetService resultSetService;

    /** Predict all the features (open, close, low, high prices and volume) of a stock one-day ahead */
    public ResultSet forecastAllCategories(MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min, ForecastJobsDataSetIterator iterator, TimePeriod timePeriod) {

        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(iterator.getExampleLength() - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }

        System.out.println("Printing predicted and actual values...");
        System.out.println("Predict, Actual");
        for (int i = 0; i < predicts.length; i++){
            System.out.println(predicts[i] + "\t" + actuals[i]);
        }

        List<Result> resultList = new ArrayList<>();
        Instant instant = Instant.now();
        ResultSet resultSet = resultSetService.saveResultSet(new ResultSet(
                instant.toEpochMilli(),
                resultList,
                TimePeriod.ONE_DAY
        ));

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
                        TimePeriod.ONE_MINUTE,
                        resultSet.getId()
                );
                resultList.add(resultService.saveResult(result));
                resultList.add(result);
            }
        }

        resultSetService.saveResultSet(resultSet);
        return resultSet;
    }
}
