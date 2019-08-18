package br.com.codefleck.criptoclima.jobs;

import br.com.codefleck.criptoclima.Utils.CsvFileWriterUtil;
import br.com.codefleck.criptoclima.Utils.StockDataUtil;
import br.com.codefleck.criptoclima.Utils.TimeSeriesUtil;
import br.com.codefleck.criptoclima.enitities.*;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@Component
public class ScheduledJobs {

    @Autowired
    NeuralNetTrainingService neuralNetTrainingService;
    @Autowired
    StockDataUtil stockDataUtil;
    @Autowired
    CandleService candleService;
    @Autowired
    CsvFileWriterUtil csvFileWriterUtil;
    @Autowired
    ResultSetService resultSetService;

    @Async
    @Scheduled(cron = "0 0/5 * * * ?",zone = "America/Sao_Paulo") //job executes every 5 min.
    public void updateForecastForHomePageJob() {
        System.out.println("JOB -> executing updateForecasetForhomePageJob...");

        synchronized(this) {

            List<Candle> candleList = candleService.findLast75DaysCandles();
            Collections.reverse(candleList);

            //case not enough candles for neural net we'll fill it up with some historical data
            if (candleList.size() < 1800){
                List<Candle> extraCandles = getExtraCandles(1800-candleList.size());
                for (int i=0; i<extraCandles.size(); i++){
                    candleList.add(extraCandles.get(i));
                }
            }
            //case we have more than 1800 candles we'll downsize the list
            try {
                csvFileWriterUtil.writeCsvFileForNeuralNets(candleList.subList(0, 1800));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String content = getCSVContent();

            //example length tests with candleList.size()=1800
            //360 = error
            //359 = error
            //358 = 1 (aparece zero no grafico, mas 1 resultado no log)
            //355 = 4 (aparece zero no grafico, mas 4 no log)
            //350 = 9 (aparece zero no grafico, mas 9 no log)
            //exampleLength is 1800 candles * 0.8 (split) = 1440 -> 1440 - 1800 = 360 (exampleLength)
            StockDataSetIterator iterator = new StockDataSetIterator(content, "BTC", 32, 358, 0.8, PriceCategory.ALL);
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
            ResultSet resultSet = neuralNetTrainingService.predictAllCategories(net, forecastData, max, min);
            resultSetService.saveResultSet(resultSet);

            System.out.println("Finished updateForecastForHomePageJob");
        }
    }

    private List<Candle> getExtraCandles(int n) {

        File filename = new File("data/BTCUSD_1h_01jan2015_14ago2019.csv");

        List<StockData> stockDataList = new ArrayList<>();
        try {
            List<String[]> list = new CSVReader(new FileReader(filename)).readAll();

            boolean isFileHeader = true;

            for (String[] arr : list) {
                if (isFileHeader) {
                    isFileHeader = false;
                    continue;
                }
                for (int i = 0; i < arr.length; i++) {
                    stockDataList.add(new StockData(arr[0], "BTC", Double.valueOf(arr[2]), Double.valueOf(arr[3]), Double.valueOf(arr[4]), Double.valueOf(arr[5]), Double.valueOf(arr[6])));
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        List<Candle> candleList = stockDataUtil.tranformStockDataInCandle(stockDataList.subList(0, n));
        return candleList;
    }

    @NotNull
    private String getCSVContent() {
        File file = new File(System.getProperty("user.home") + "/csv/forecastData.csv");
        if (!file.exists()){
            System.out.println("File Found : " + file.getName());
        }

        return file.getPath();
    }
}

