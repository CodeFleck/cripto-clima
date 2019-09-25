package br.com.codefleck.criptoclima.jobs;

import br.com.codefleck.criptoclima.Utils.CsvFileWriterUtil;
import br.com.codefleck.criptoclima.Utils.StockDataUtil;
import br.com.codefleck.criptoclima.Utils.TimeSeriesUtil;
import br.com.codefleck.criptoclima.enitities.*;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.ForecastServiceImpl;
import br.com.codefleck.criptoclima.services.LatestPriceService;
import br.com.codefleck.criptoclima.services.NeuralNetTrainingService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    @Autowired
    LatestPriceService latestPriceService;

    private TimeSeriesUtil timeSeriesUtil = new TimeSeriesUtil();
    private StockDataUtil stockDataUtil = new StockDataUtil();

    final int PREDICTION_CANDLE_LIST_SIZE = 30;

    @Async
    @Scheduled(cron = "0 0/10 * * * ?",zone = "America/Sao_Paulo") //job executes every 10 min.
    public void updateDailyForecastJob() {
        System.out.println("JOB -> executing updateDailyForecasetJob...");

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

        System.out.println("Finished updateDailyForecasetJob");
    }

    @Async
    @Scheduled(cron = "0 0/12 * * * ?",zone = "America/Sao_Paulo")
    public void updateTwoDaysForecastJob() {
        System.out.println("JOB -> executing updateTwoDaysForecastJob...");

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

        System.out.println("Finished updateTwoDaysForecastJob");
    }

    @Async
    @Scheduled(cron = "0 0/14 * * * ?",zone = "America/Sao_Paulo")
    public void updateThreeDaysForecastJob() {
        System.out.println("JOB -> executing updateThreeDaysForecastJob...");

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

        System.out.println("Finished updateThreeDaysForecastJob");
    }

    @Async
    @Scheduled(cron = "0 0/16 * * * ?",zone = "America/Sao_Paulo")
    public void updateFourDaysForecastJob() {
        System.out.println("JOB -> executing updateFourDaysForecastJob...");

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

        System.out.println("Finished updateFourDaysForecastJob");
    }

    @Async
    @Scheduled(cron = "0 0/18 * * * ?",zone = "America/Sao_Paulo")
    public void updateFiveDaysForecastJob() {
        System.out.println("JOB -> executing updateFiveDaysForecastJob...");

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

        System.out.println("Finished updateFiveDaysForecastJob");
    }

    @Async
    @Scheduled(cron = "0 0/20 * * * ?",zone = "America/Sao_Paulo")
    public void updateSixDaysForecastJob() {
        System.out.println("JOB -> executing updateSixDaysForecastJob...");

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

        System.out.println("Finished updateSixDaysForecastJob");
    }

    @Async
    @Scheduled(cron = "0 0/1 * * * ?",zone = "America/Sao_Paulo") //job executes every 1 min.
    public void getLatestBTCPriceJob() {

        URL url = null;
        try {
            url = new URL("https://api.bitfinex.com/v1/pubticker/btcusd");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            int status = con.getResponseCode();
            Reader streamReader = null;
            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JsonObject gson = new Gson().fromJson(content.toString(), JsonObject.class);
                LatestPrice latestPrice = new LatestPrice();
                latestPrice.setCoin("btc");
                JsonElement timeStamp = gson.get("timestamp");
                JsonElement latest_Price = gson.get("last_price");
                latestPrice.setTimestamp(timeStamp.toString());
                latestPrice.setLatestPrice(latest_Price.getAsDouble());
                latestPriceService.saveLatestPrice(latestPrice);
            }
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @NotNull
    private String getCSVContent(TimePeriod timePeriod) {
        File file = new File("data/homePage_update_forecastData_".concat(timePeriod.toString()).concat(".csv"));
        if (!file.exists()){
            System.out.println("File Found : " + file.getName());
        }

        return file.getPath();
    }
}

//Example length calculation
//if exampleLength is 1800 candles then exampleLength=360. (1800 * 0.8 (split) = 1440 -> 1440 - 1800 = 360)