package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.StockData;
import com.opencsv.CSVReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
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
        Collections.reverse(stockDataList);

        //write new csv
        createNewCsv(stockDataList);
    }

    private static void createNewCsv(List<StockData> stockDataList) {
        CsvFileWriterUtil csvFileWriterUtil = new CsvFileWriterUtil();
        try {
            csvFileWriterUtil.writeCsvFileWithStockDataList(stockDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static String getCSVFilePath() {
        File file = new File("data/BTCUSD_1h_01jan2015_14ago2019.csv");
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

}
