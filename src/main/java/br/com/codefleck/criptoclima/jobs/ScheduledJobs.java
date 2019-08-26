package br.com.codefleck.criptoclima.jobs;

import br.com.codefleck.criptoclima.Utils.CsvFileWriterUtil;
import br.com.codefleck.criptoclima.Utils.StockDataUtil;
import br.com.codefleck.criptoclima.enitities.*;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.ForecastServiceImpl;
import br.com.codefleck.criptoclima.services.NeuralNetTrainingService;
import br.com.codefleck.criptoclima.services.ResultSetService;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Component
public class ScheduledJobs {

    @Autowired
    NeuralNetTrainingService neuralNetTrainingService;
    @Autowired
    ForecastServiceImpl forecastService;
    @Autowired
    StockDataUtil stockDataUtil;
    @Autowired
    CandleService candleService;
    @Autowired
    CsvFileWriterUtil csvFileWriterUtil;

    @Async
    @Scheduled(cron = "0 0/1 * * * ?",zone = "America/Sao_Paulo") //job executes every 30 min.
    public void updateDailyForecastForHomePageJob() {
        System.out.println("JOB -> executing updateDailyForecasetForhomePageJob...");

        final int DAILY_PREDICTION_CANDLE_LIST_SIZE = 30;

        List<Candle> candleList = candleService.findLast30DaysCandles();

        //case not enough candles for neural net we'll fill it up with some historical data
        if (candleList.size() < DAILY_PREDICTION_CANDLE_LIST_SIZE) {
            List<Candle> extraCandles = getExtraDailyCandles((DAILY_PREDICTION_CANDLE_LIST_SIZE - candleList.size()));
            extraCandles.forEach(candle -> candleList.add(candle));
        }

        //case we have more candles than needed we'll downsize the list
        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, DAILY_PREDICTION_CANDLE_LIST_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent();

        //if exampleLength is 1800 candles * 0.8 (split) = 1440 -> 1440 - 1800 = 360 (360 = exampleLength)
        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 28, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File locationToSave = new File("models/StockPriceLSTM_ALL.zip");
        System.out.println("Restoring model...");
        MultiLayerNetwork net = RecurrentNets.createAndBuildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Evaluating...");
        INDArray max = Nd4j.create(iterator.getMaxArray());
        INDArray min = Nd4j.create(iterator.getMinArray());
        ResultSet result = forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.ONE_DAY);

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
    private String getCSVContent() {
        File file = new File("data/homePage_update_forecastData.csv");
        if (!file.exists()){
            System.out.println("File Found : " + file.getName());
        }

        return file.getPath();
    }
}
