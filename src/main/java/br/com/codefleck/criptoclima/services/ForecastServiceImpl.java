package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.CustomStockData;
import br.com.codefleck.criptoclima.enitities.ForecastJobsDataSetIterator;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
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

    public ResultSet forecastAllCategories(MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min, ForecastJobsDataSetIterator iterator, TimePeriod timePeriod) {

        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];

        int forecastIndex = 1;

        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(iterator.getExampleLength() - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }

        System.out.println("Printing prediction results for weekday #" + forecastIndex + "...");
        System.out.println("Predict, Actual");
        for (int i = 0; i < predicts.length; i++){
            System.out.println(predicts[i] + "\t" + actuals[i]);
        }

        List<Result> resultList = new ArrayList<>();
        Instant instant = Instant.now();
        ResultSet resultSet = resultSetService.saveResultSet(new ResultSet(
                instant.toEpochMilli(),
                resultList,
                timePeriod
        ));

        for (int n = 0; n < 6; n++) {

            PriceCategory priceCategoryForResult;
            switch (n) {
                case 0: priceCategoryForResult = PriceCategory.OPEN; break;
                case 1: priceCategoryForResult = PriceCategory.CLOSE; break;
                case 2: priceCategoryForResult = PriceCategory.LOW; break;
                case 3: priceCategoryForResult = PriceCategory.HIGH; break;
                case 4: priceCategoryForResult = PriceCategory.VOLUME; break;
                case 5: priceCategoryForResult = PriceCategory.DAILY_CHANGE_PERC; break;
                default: throw new NoSuchElementException();
            }

            for (int i = 0; i < predicts.length; i++) {
                Result result = new Result(
                        priceCategoryForResult,
                        Double.valueOf(predicts[i].getDouble(n)),
                        Double.valueOf(actuals[i].getDouble(n)),
                        resultSet.getPeriod(),
                        resultSet.getId(),
                        forecastIndex
                );
                resultList.add(resultService.saveResult(result));
            }
        }

        //grab current prediction, feeds into neural net again to predict the following turn.
        forecastIndex++;
        ResultSet resultSetForWeekDay2 = predictFollowingWeek(resultSet, iterator, net, forecastIndex);
        forecastIndex++;
        ResultSet resultSetForWeekDay3 = predictFollowingWeek(resultSetForWeekDay2, iterator, net, forecastIndex);
        forecastIndex++;
        ResultSet resultSetForWeekDay4 = predictFollowingWeek(resultSetForWeekDay3, iterator, net, forecastIndex);
        forecastIndex++;
        ResultSet resultSetForWeekDay5 = predictFollowingWeek(resultSetForWeekDay4, iterator, net, forecastIndex);
        forecastIndex++;
        return predictFollowingWeek(resultSetForWeekDay5, iterator, net, forecastIndex);
    }

    private ResultSet predictFollowingWeek(ResultSet resultSet, ForecastJobsDataSetIterator iterator, MultiLayerNetwork net, int forecastIndex) {

        CustomStockData customStockData = new CustomStockData();
        Instant instant = Instant.now();
        customStockData.setDate(String.valueOf(instant.toEpochMilli()));
        customStockData.setSymbol("BTC");

        List<Result> resultList = resultSet.getResultList();

        for (int i = 0; i < resultList.size(); i++) {
            if (resultList.get(i).getResultsForWeekDay() == (forecastIndex-1)) {
                if (resultList.get(i).getPriceCategory() == PriceCategory.OPEN) {
                    customStockData.setOpen(resultList.get(i).getPrediction());
                } else if (resultList.get(i).getPriceCategory() == PriceCategory.CLOSE) {
                    customStockData.setClose(resultList.get(i).getPrediction());
                } else if (resultList.get(i).getPriceCategory() == PriceCategory.LOW) {
                    customStockData.setLow(resultList.get(i).getPrediction());
                } else if (resultList.get(i).getPriceCategory() == PriceCategory.HIGH) {
                    customStockData.setHigh(resultList.get(i).getPrediction());
                } else if (resultList.get(i).getPriceCategory() == PriceCategory.VOLUME) {
                    customStockData.setVolume(resultList.get(i).getPrediction());
                } else if (resultList.get(i).getPriceCategory() == PriceCategory.DAILY_CHANGE_PERC) {
                    customStockData.setDailyChangePercentage(resultList.get(i).getPrediction());
                }
            }
        }

        List<CustomStockData> dataToBeForecasted = new ArrayList<>();

        //adding 2 mock stockData lines in the list to be able to generate test dataset properly
        if (dataToBeForecasted.size() <= 1){
            dataToBeForecasted.add(new CustomStockData());
            dataToBeForecasted.add(new CustomStockData());
        }
        dataToBeForecasted.add(1, customStockData);

        iterator.reset();
        iterator.setExampleLength(1);
        List<Pair<INDArray, INDArray>> pairList = iterator.generateTestDataSet(dataToBeForecasted);
        dataToBeForecasted.clear();

        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());

        List<INDArray> predicts = new ArrayList<>();
        List<INDArray> actuals = new ArrayList<>();
        for (int i = 0; i < pairList.size(); i++) {
            predicts.add(net.rnnTimeStep(pairList.get(i).getKey()).getRow(iterator.getExampleLength() - 1).mul(max.sub(min)).add(min));
            actuals.add(pairList.get(i).getValue());
        }
        pairList.clear();

        System.out.println("Printing prediction results for weekday #" + forecastIndex + "...");
        System.out.println("Predict, Actual");
        for (int i = 0; i < predicts.size(); i++) {
            System.out.println(predicts.get(i) + "\t" + actuals.get(i));
        }

        for (int n = 0; n < 6; n++) {

            PriceCategory priceCategoryForResult;
            switch (n) {
                case 0: priceCategoryForResult = PriceCategory.OPEN; break;
                case 1: priceCategoryForResult = PriceCategory.CLOSE; break;
                case 2: priceCategoryForResult = PriceCategory.LOW; break;
                case 3: priceCategoryForResult = PriceCategory.HIGH; break;
                case 4: priceCategoryForResult = PriceCategory.VOLUME; break;
                case 5: priceCategoryForResult = PriceCategory.DAILY_CHANGE_PERC; break;
                default:
                    throw new NoSuchElementException();
            }

            for (int x = 0; x < predicts.size(); x++) {
                Result result = new Result(
                        priceCategoryForResult,
                        Double.valueOf(predicts.get(x).getDouble(n)),
                        Double.valueOf(actuals.get(x).getDouble(n)),
                        resultSet.getPeriod(),
                        resultSet.getId(),
                        forecastIndex
                );
                resultList.add(resultService.saveResult(result));
            }
        }
        predicts.clear();
        actuals.clear();

        return resultSetService.saveResultSet(resultSet);
    }
}
