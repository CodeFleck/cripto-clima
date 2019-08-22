package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class CsvFileWriterUtil {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "date,symbol,open,close,low, high, volume";


    public void writeCsvFileForNeuralNets(List<Candle> candleList) throws IOException {

        String csvFileForTrainingNeuralNets = System.getProperty("user.home") + "/csv/homePage_update_forecastData.csv";

        FileWriter fileWriter = new FileWriter(csvFileForTrainingNeuralNets);
        try {
            fileWriter.append(FILE_HEADER).append(NEW_LINE_SEPARATOR);
            for (Candle candle : candleList) {
                if (candle == null) {
                    break;
                }
                fileWriter.append(String.valueOf(candle.getTimestamp())).append(COMMA_DELIMITER)
                        .append(("BTC")).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getOpen())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getClose())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getLow())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getHigh())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getVolume())).append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file for created successfully!");
            Thread.sleep(4000); //need this time for the file to unlock before is ready to use

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

    public void writeCsvFileWithStockDataList(List<StockData> stockDataList) throws IOException {

        String csvFileForTrainingNeuralNets = System.getProperty("user.home") + "/csv/changeMe.csv";

        FileWriter fileWriter = new FileWriter(csvFileForTrainingNeuralNets);
        try {
            fileWriter.append(FILE_HEADER).append(NEW_LINE_SEPARATOR);
            for (StockData stock : stockDataList) {
                if (stock == null) {
                    break;
                }

                fileWriter.append(stock.getDate()).append(COMMA_DELIMITER)
                        .append(stock.getSymbol()).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getOpen())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getClose())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getLow())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getHigh())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stock.getVolume())).append(NEW_LINE_SEPARATOR);
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

