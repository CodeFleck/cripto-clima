package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvFileWriterUtil {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "date,symbol,open,close,low, high, volume";


    public void writeCsvFileForNeuralNets(List<Candle> candleList) throws IOException {

        String csvFileForTrainingNeuralNets = System.getProperty("user.home") + "/csv/forecastData.csv";

        FileWriter fileWriter = new FileWriter(csvFileForTrainingNeuralNets);
        try {
            //Write the CSV file header
            fileWriter.append(FILE_HEADER).append(NEW_LINE_SEPARATOR);
            for (Candle candle : candleList) {
                if (candle == null) {
                    break;
                }

                //LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(candle.getTimestamp()), ZoneId.systemDefault());

                fileWriter.append(String.valueOf(candle.getTimestamp())).append(COMMA_DELIMITER) //check if it is creating the correct date in csv file for training Nets (need time and minute)
                        .append(("BTC")).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getOpen())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getClose())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getLow())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getHigh())).append(COMMA_DELIMITER)
                        .append(String.valueOf(candle.getVolume())).append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file for created successfully!");
            Thread.sleep(4000);

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

