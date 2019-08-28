package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class StockDataUtil {

    public List<StockData> tranformCandleInStockData(List<Candle> candleList){

        List<StockData> stockDataList = new ArrayList<>();

        for (Candle candle : candleList) {
            StockData stockData = new StockData(
                String.valueOf(candle.getTimestamp()),
                    "BTC",
                    candle.getOpen(),
                    candle.getClose(),
                    candle.getLow(),
                    candle.getHigh(),
                    candle.getVolume()
            );
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    public List<Candle> tranformStockDataInCandle(List<StockData> stockDataList) {

        List<Candle> candleList = new ArrayList<>();

        for (StockData stock: stockDataList) {

            Timestamp timestamp = convertStringToTimestamp(stock.getDate());
            
            Candle candle = new Candle(
                    timestamp.getTime(),
                    stock.getOpen(),
                    stock.getClose(),
                    stock.getLow(),
                    stock.getHigh(),
                    stock.getVolume()
            );
              candleList.add(candle);
        }
        return candleList;
    }

    public static Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy/MM/dd");
            Date date = (Date) formatter.parse(str_date);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }
}
