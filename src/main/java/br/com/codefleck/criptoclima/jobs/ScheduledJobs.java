package br.com.codefleck.criptoclima.jobs;

import br.com.codefleck.criptoclima.Utils.CsvFileWriterUtil;
import br.com.codefleck.criptoclima.Utils.StockDataUtil;
import br.com.codefleck.criptoclima.enitities.*;
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

    private StockDataUtil stockDataUtil = new StockDataUtil();

    final int PREDICTION_CANDLE_LIST_SIZE = 3;

    @Async
    @Scheduled(cron = "0 0/10 * * * ?",zone = "America/Sao_Paulo") //job executes every 10 min.
    public void updateDailyForecastJob() {
        System.out.println("JOB -> executing updateDailyForecasetJob...");

        TimePeriod timePeriod = TimePeriod.ONE_DAY;

        List<Candle> candleList = candleService.findLastHourCandles();//comes aggregated in one minute
        Collections.reverse(candleList); //we want most recent candle at index 0

        //case not enough candles for neural net we'll fill it up with some historical data
        if (candleList.size() < PREDICTION_CANDLE_LIST_SIZE) {
            List<Candle> extraCandles = getExtraCandles((PREDICTION_CANDLE_LIST_SIZE - candleList.size()));
            extraCandles.forEach(candle -> candleList.add(candle));
        }

        List<CustomStockData> customStockDataList = stockDataUtil.tranformCandleInCustomStockData(candleList);
        //case we have more candles than needed we'll downsize the list
        try {
            csvFileWriterUtil.writeCsvFileForNeuralNets(customStockDataList.subList(0, PREDICTION_CANDLE_LIST_SIZE), timePeriod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = getCSVContent(timePeriod);

        ForecastJobsDataSetIterator iterator = new ForecastJobsDataSetIterator(content, 1, 1, PriceCategory.ALL);
        List<Pair<INDArray, INDArray>> forecastData = iterator.getTestDataSet();

        File model = new File("models/StockPriceLSTM__ALL.zip");
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
        forecastService.forecastAllCategories(net, forecastData, max, min, iterator, TimePeriod.ONE_DAY);

        System.out.println("Finished updateDailyForecasetJob");
    }

    @Async
    @Scheduled(cron = "0 0/1 * * * ?",zone = "America/Sao_Paulo") //job executes every 1 min.
    public void getLatestBTCPriceJob() {

        URL url;
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
                stockDataList.add(new StockData(arr[0], "BTC", Double.valueOf(arr[2]), Double.valueOf(arr[3]), Double.valueOf(arr[4]), Double.valueOf(arr[5]), Double.valueOf(arr[6])));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        Collections.reverse(stockDataList);
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