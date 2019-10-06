package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.CustomStockData;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class CsvFileWriterUtil {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "date,symbol,open,close,low, high, volume, dailyChangePercentage";


    public void writeCsvFileForNeuralNets(List<CustomStockData> customStockData, TimePeriod timePeriod) throws IOException {

        String csvFileForTrainingNeuralNets = "data/homePage_update_forecastData_".concat(timePeriod.toString()).concat(".csv");

        FileWriter fileWriter = new FileWriter(csvFileForTrainingNeuralNets);
        try {
            fileWriter.append(FILE_HEADER).append(NEW_LINE_SEPARATOR);
            for (CustomStockData stockData : customStockData) {
                if (stockData == null) {
                    break;
                }
                fileWriter.append(String.valueOf(stockData.getDate())).append(COMMA_DELIMITER)
                        .append(("BTC")).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getOpen())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getClose())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getLow())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getHigh())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getVolume())).append(COMMA_DELIMITER)
                        .append(String.valueOf(stockData.getDailyChangePercentage())).append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file created successfully!");
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
}

