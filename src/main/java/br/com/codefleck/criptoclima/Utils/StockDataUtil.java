package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.springframework.stereotype.Component;

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
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getVolume()
            );
            stockDataList.add(stockData);
        }
        return stockDataList;
    }
}
