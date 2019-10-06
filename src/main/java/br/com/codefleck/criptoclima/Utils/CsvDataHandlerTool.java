package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.CustomStockData;
import br.com.codefleck.criptoclima.enitities.StockData;
import com.opencsv.CSVReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CsvDataHandlerTool {

    public static void main(String[] args) {

        final String PATH = getCSVFilePath();

        //load csv data
        List<StockData> stockDataList = loadCsv(PATH);

        //do something
        List<CustomStockData> stockDataListWithDailyChangePerc = transformStockDataInCustomStockData(stockDataList);

        //write new csv
        try {
            createNewCsv(stockDataListWithDailyChangePerc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<CustomStockData> transformStockDataInCustomStockData(List<StockData> stockDataList) {

        List<CustomStockData> customStockDataList = new ArrayList<>();

        for (int i=0; i<stockDataList.size(); i++) {
            if (i == 0){
                CustomStockData customStockData = new CustomStockData();
                customStockData.setDate(String.valueOf(stockDataList.get(i).getDate()));
                customStockData.setSymbol("BTC");
                customStockData.setOpen(stockDataList.get(i).getOpen());
                customStockData.setClose(stockDataList.get(i).getClose());
                customStockData.setLow(stockDataList.get(i).getLow());
                customStockData.setHigh(stockDataList.get(i).getHigh());
                customStockData.setVolume(stockDataList.get(i).getVolume());
                customStockData.setDailyChangePercentage(0);
                customStockDataList.add(customStockData);
            } else {
                CustomStockData customStockData = new CustomStockData();
                customStockData.setDate(String.valueOf(stockDataList.get(i).getDate()));
                customStockData.setSymbol("BTC");
                customStockData.setOpen(stockDataList.get(i).getOpen());
                customStockData.setClose(stockDataList.get(i).getClose());
                customStockData.setLow(stockDataList.get(i).getLow());
                customStockData.setHigh(stockDataList.get(i).getHigh());
                customStockData.setVolume(stockDataList.get(i).getVolume());
                customStockData.setDailyChangePercentage(round(calculatePercentage(stockDataList.get(i).getClose(), stockDataList.get(i-1).getClose())));
                customStockDataList.add(customStockData);
            }
        }

        return customStockDataList;
    }

    public static double calculatePercentage(double currentPrice, double previousPrice) {
        double variationAmmount = (currentPrice - previousPrice);
        double result = (variationAmmount*100) / previousPrice;
        return result;
    }

    private static double round(double n){
        return Math.round(n * 10000d) / 10000d;
    }

    @NotNull
    private static String getCSVFilePath() {
        File file = new File("data/(changeME)BTCUSD_1h_01jan2015_14ago2019.csv");
        System.out.println("File Found : " + file.exists());

        return file.getPath();
    }

    private static List<StockData> loadCsv(String path) {
        List<StockData> stockDataList = new ArrayList<>();
        try {
            List<String[]> list = new CSVReader(new FileReader(path)).readAll();

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
        return stockDataList;
    }

    private static void createNewCsv(List<CustomStockData> stockDataList) throws IOException {

        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String FILE_HEADER = "date,symbol,open,close,low, high, volume, dailyChangePercentage";

        String csvFileForTrainingNeuralNets = "data/(ChangeMe)1hourFullDataSet(01Jan15-14Ago19).csv";

        FileWriter fileWriter = new FileWriter(csvFileForTrainingNeuralNets);
        try {
            fileWriter.append(FILE_HEADER).append(NEW_LINE_SEPARATOR);
            for (CustomStockData stock : stockDataList) {
                if (stock == null) {
                    break;
                }

                fileWriter.append(stock.getDate()).append(COMMA_DELIMITER)
                        .append(stock.getSymbol()).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getOpen())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getClose())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getLow())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getHigh())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getVolume())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getDailyChangePercentage())).append(NEW_LINE_SEPARATOR);;
            }
            System.out.println("CSV file for created successfully!");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter");
                e.printStackTrace();
            }
        }
    }
}
