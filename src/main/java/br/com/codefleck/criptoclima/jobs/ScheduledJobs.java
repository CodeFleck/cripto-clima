package br.com.codefleck.criptoclima.jobs;

import br.com.codefleck.criptoclima.Utils.CsvFileWriterUtil;
import br.com.codefleck.criptoclima.Utils.StockDataUtil;
import br.com.codefleck.criptoclima.Utils.TimeSeriesUtil;
import br.com.codefleck.criptoclima.enitities.*;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.ForecastServiceImpl;
import br.com.codefleck.criptoclima.services.NeuralNetTrainingService;
import com.opencsv.CSVReader;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@Component
public class ScheduledJobs {

    @Autowired
    NeuralNetTrainingService neuralNetTrainingService;
    @Autowired
    ForecastServiceImpl forecastService;
    @Autowired
    CandleService candleService;
    @Autowired
    CsvFileWriterUtil csvFileWriterUtil;

    private TimeSeriesUtil timeSeriesUtil = new TimeSeriesUtil();
    private StockDataUtil stockDataUtil = new StockDataUtil();

    final int PREDICTION_CANDLE_LIST_SIZE = 30;

    @Async
    @Scheduled(cron = "0 0/25 * * * ?",zone = "America/Sao_Paulo") //job executes every 28 min.
    public void updateDailyForecastForHomePageJob() {
        System.out.println("JOB -> executing updateDailyForecasetForhomePageJob...");

        TimePeriod timePeriod = TimePeriod.ONE_DAY;

        List<Candle> candleList = candleService.findLast30DaysCandles();//comes aggregated in one day periods

        //case not enough candles for neural net we'll fill it up with some historical data
        if (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            List<Candle> extraCandles = getExtraDailyCandles((PREDICTION_CANDLE_LIST_SIZE - candleList.size()));
            extraCandles.forEach(candle -> candleList.add(candle));
        }

        //case we have more candles than needed we'll downsize the list
        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        //if exampleLength is 1800 candles * 0.8 (split) = 1440 -> 1440 - 1800 = 360 (360 = exampleLength)
        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_DAILY.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.ONE_DAY);

        System.out.println("Finished updateDailyForecasetForhomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/26 * * * ?",zone = "America/Sao_Paulo")
    public void updateTwoDaysForecastForHomePageJob() {
        System.out.println("JOB -> executing updateTwoDaysForecastForHomePageJob...");

        TimePeriod timePeriod = TimePeriod.TWO_DAYS;

        List<Candle> candleList = candleService.findLast60DaysCandles(); //comes aggregated in two days

        while (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInTwoDaysPeriod = PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInTwoDaysPeriod*3;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInTwoDays = timeSeriesUtil.aggregateTimeSeriesToTwoDays(extraCandles);
            extraCandlesAggregatedInTwoDays.forEach(candle -> candleList.add(candle));
        }

        //case we have more candles than needed we'll downsize the list
        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_TWODAYS.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.TWO_DAYS);

        System.out.println("Finished updateTwoDaysForecastForHomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/27 * * * ?",zone = "America/Sao_Paulo")
    public void updateThreeDaysForecastForHomePageJob() {
        System.out.println("JOB -> executing updateThreeDaysForecastForHomePageJob...");

        TimePeriod timePeriod = TimePeriod.THREE_DAYS;

        List<Candle> candleList = candleService.findLast90DaysCandles(); //comes aggregated in three days

        while (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInThreeDaysPeriod = PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInThreeDaysPeriod*3;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInThreeDays = timeSeriesUtil.aggregateTimeSeriesToThreeDays(extraCandles);
            extraCandlesAggregatedInThreeDays.forEach(candle -> candleList.add(candle));
        }

        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_THREEDAYS.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.THREE_DAYS);

        System.out.println("Finished updateThreeDaysForecastForHomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/28 * * * ?",zone = "America/Sao_Paulo")
    public void updateFourDaysForecastForHomePageJob() {
        System.out.println("JOB -> executing updateFourDaysForecastForHomePageJob...");

        TimePeriod timePeriod = TimePeriod.FOUR_DAYS;

        List<Candle> candleList = candleService.findLast120DaysCandles(); //comes aggregated in four days

        while (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInFourDaysPeriod = PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInFourDaysPeriod*3;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInFourDays = timeSeriesUtil.aggregateTimeSeriesToFourDays(extraCandles);
            extraCandlesAggregatedInFourDays.forEach(candle -> candleList.add(candle));
        }

        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_FOURDAYS.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.FOUR_DAYS);

        System.out.println("Finished updateFourDaysForecastForHomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/1 * * * ?",zone = "America/Sao_Paulo")
    public void updateFiveDaysForecastForHomePageJob() {
        System.out.println("JOB -> executing updateFiveDaysForecastForHomePageJob...");

        TimePeriod timePeriod = TimePeriod.FIVE_DAYS;

        List<Candle> candleList = candleService.findLast190DaysCandles(); //comes aggregated in five days

        while (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInFiveDaysPeriod = PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInFiveDaysPeriod*3;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInFiveDays = timeSeriesUtil.aggregateTimeSeriesToFiveDays(extraCandles);
            extraCandlesAggregatedInFiveDays.forEach(candle -> candleList.add(candle));
        }

        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_FIVEDAYS.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.FIVE_DAYS);

        System.out.println("Finished updateFiveDaysForecastForHomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/30 * * * ?",zone = "America/Sao_Paulo")
    public void updateSixDaysForecastForHomePageJob() {
        System.out.println("JOB -> executing updateSixDaysForecastForHomePageJob...");

        TimePeriod timePeriod = TimePeriod.ONE_WEEK;

        List<Candle> candleList = candleService.findLast210DaysCandles(); //comes aggregated into one week periods

        while (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInSixDaysPeriod = PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInSixDaysPeriod*5;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInSixDays = timeSeriesUtil.aggregateTimeSeriesToSixDays(extraCandles);
            extraCandlesAggregatedInSixDays.forEach(candle -> candleList.add(candle));
        }

        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        //if exampleLength is 1800 candles * 0.8 (split) = 1440 -> 1440 - 1800 = 360 (360 = exampleLength)
        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL_WEEKLY.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.ONE_WEEK);

        System.out.println("Finished updateSixDaysForecastForHomePageJob");
    }

    private List<Candle> getExtraDailyCandles(int n) {
        File filename = new File("data/Kraken_BTCUSD_d.csv");
        List<StockData> stockDataList = new ArrayList<>();
        try {
            List<String[]> list = new CSVReader(new FileReader(filename)).readAll();
            boolean isFileHeader = true;
            for (String[] arr : list) {
                if (isFileHeader) {
                    isFileHeader = false;
                    continue;
                }
                stockDataList.add(new StockData(arr[0], "BTC", Double.valueOf(arr[2]), Double.valueOf(arr[3]), Double.valueOf(arr[4]), Double.valueOf(arr[5]), Double.valueOf(arr[6])));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        List<Candle> candleList = stockDataUtil.tranformStockDataInCandle(stockDataList.subList(0, n));

        return candleList;
    }

    private List<Candle> getExtraOneMinuteCandles(int n) {
        File filename = new File("data/coinBaseDailyCloseDez2014-Jan2018.csv");
        List<StockData> stockDataList = new ArrayList<>();
        try {
            List<String[]> list = new CSVReader(new FileReader(filename)).readAll();
            boolean isFileHeader = true;
            for (String[] arr : list) {
                if (isFileHeader) {
                    isFileHeader = false;
                    continue;
                }
                stockDataList.add(new StockData(arr[0], "BTC", Double.valueOf(arr[2]), Double.valueOf(arr[3]), Double.valueOf(arr[4]), Double.valueOf(arr[5]), Double.valueOf(arr[6])));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        List<Candle> candleList = stockDataUtil.tranformStockDataInCandle(stockDataList.subList((stockDataList.size() - n), stockDataList.size()));

        return candleList;
    }

    @NotNull
    private String getCSVContent(TimePeriod timePeriod) {
        File file = new File("data/homePage_update_forecastData_".concat(timePeriod.toString()).concat(".csv"));
        if (!file.exists()){
            System.out.println("File Found : " + file.getName());
        }

        return file.getPath();
    }
}
