package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            //will receive the wrong date for now. Otherwise have to deal with parsing String date to long millis
            Instant instant = Instant.now();

            Candle candle = new Candle(
                    instant.toEpochMilli(),
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
}
