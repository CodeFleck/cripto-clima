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

    @Async
    @Scheduled(cron = "0 0/28 * * * ?",zone = "America/Sao_Paulo") //job executes every 28 min.
    public void updateDailyForecastForHomePageJob() {
        System.out.println("JOB -> executing updateDailyForecasetForhomePageJob...");

        final int DAILY_PREDICTION_CANDLE_LIST_SIZE = 30; //30 days
        TimePeriod timePeriod = TimePeriod.ONE_DAY;

        List<Candle> candleList = candleService.findLast30DaysCandles();//comes aggregated in one day periods

        //case not enough candles for neural net we'll fill it up with some historical data
        if (candleList.size() < DAILY_PREDICTION_CANDLE_LIST_SIZE) {
            List<Candle> extraCandles = getExtraDailyCandles((DAILY_PREDICTION_CANDLE_LIST_SIZE - candleList.size()));
            extraCandles.forEach(candle -> candleList.add(candle));
        }

        //case we have more candles than needed we'll downsize the list
        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, DAILY_PREDICTION_CANDLE_LIST_SIZE), timePeriod);
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

        System.out.println("Finished updateForecastForHomePageJob");
    }

    @Async
    @Scheduled(cron = "0 0/30 * * * ?",zone = "America/Sao_Paulo") //job executes every 30 min.
    public void update7DaysForecastForHomePageJob() {
        System.out.println("JOB -> executing update7DaysForecastForHomePageJob...");

        final int WEEKLY_PREDICTION_CANDLE_LIST_SIZE = 30; //210 days, 30 weeks
        TimePeriod timePeriod = TimePeriod.ONE_WEEK;

        List<Candle> candleList = candleService.findLast210DaysCandles(); //comes aggregated into one week periods

        if (candleList.size() < WEEKLY_PREDICTION_CANDLE_LIST_SIZE) {
            int extraCandlesToGetInWeeks = WEEKLY_PREDICTION_CANDLE_LIST_SIZE - candleList.size();
            int extraCandlesToGetInDays = extraCandlesToGetInWeeks*7;
            List<Candle> extraCandles = getExtraDailyCandles(extraCandlesToGetInDays);
            Collections.reverse(extraCandles);
            List<Candle> extraCandlesAggregatedInWeeks = timeSeriesUtil.aggregateTimeSeriesToSixDays(extraCandles);
            extraCandlesAggregatedInWeeks.forEach(candle -> candleList.add(candle));
        }

        //temporary fix to increase number of weeks to match exampleLength
        int k = 0;
        while (candleList.size() < 30){
            candleList.add(candleList.get(k));
            k++;
        }

        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, WEEKLY_PREDICTION_CANDLE_LIST_SIZE), timePeriod);
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

        System.out.println("Finished updateForecastForHomePageJob");
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
