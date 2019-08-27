package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TimeSeriesUtil {

    public List<Candle> aggregateTimeSeriesToOneMinute(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            long nextMinute = currentCandle.getTimestamp() + 60000;

            while (candleList.size() > i && (candleList.get(i).getTimestamp() < nextMinute)){
                currentCandle = candleList.get(i); // nextBar
                currentHigh = Math.max(currentHigh, currentCandle.getHigh());
                currentLow = Math.min(currentLow, currentCandle.getLow());
                currentVolume += currentCandle.getVolume();
                i++;
            }
            long currentEndTime = currentCandle.getTimestamp();
            double currentClose = currentCandle.getClose();
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return aggCandles;
    }

    public List<Candle> aggregateTimeSeriesToOneHour(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            long nextHour = currentCandle.getTimestamp() + 3600000;

            while (candleList.size() > i && (candleList.get(i).getTimestamp() < nextHour)){
                currentCandle = candleList.get(i); // nextBar
                currentHigh = Math.max(currentHigh, currentCandle.getHigh());
                currentLow = Math.min(currentLow, currentCandle.getLow());
                currentVolume += currentCandle.getVolume();
                i++;
            }
            long currentEndTime = currentCandle.getTimestamp();
            double currentClose = currentCandle.getClose();
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return aggCandles;
    }

    public List<Candle> aggregateTimeSeriesToOneDay(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            long nextDay = currentCandle.getTimestamp() + (3600000*24);

            while (candleList.size() > i && (candleList.get(i).getTimestamp() < nextDay)){
                currentCandle = candleList.get(i); // nextBar
                currentHigh = Math.max(currentHigh, currentCandle.getHigh());
                currentLow = Math.min(currentLow, currentCandle.getLow());
                currentVolume += currentCandle.getVolume();
                i++;
            }
            long currentEndTime = currentCandle.getTimestamp();
            double currentClose = currentCandle.getClose();
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return aggCandles;
    }

    public List<Candle> aggregateTimeSeriesToOneWeek(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            long nextDay = currentCandle.getTimestamp() + (3600000*24);
            long nextWeek = nextDay * 7;

            while (candleList.size() > i && (candleList.get(i).getTimestamp() < nextWeek)){
                currentCandle = candleList.get(i); // nextBar
                currentHigh = Math.max(currentHigh, currentCandle.getHigh());
                currentLow = Math.min(currentLow, currentCandle.getLow());
                currentVolume += currentCandle.getVolume();
                i++;
            }
            long currentEndTime = currentCandle.getTimestamp();
            double currentClose = currentCandle.getClose();
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return aggCandles;
    }

    public List<StockData> aggregateStockDataToWeek(List<StockData> stockDataList) {
        List<StockData> aggStockData = new ArrayList<>();
        for (int i = 0; i < stockDataList.size() ; i++) {
            StockData currentStock = stockDataList.get(i);
            double currentOpen = currentStock.getOpen();
            double currentLow = currentStock.getLow();
            double currentHigh = currentStock.getHigh();
            double currentVolume = currentStock.getVolume();

//            long nextDay = currentStock.getTimestamp() + (3600000*24);
            long nextWeek = 0;//nextDay * 7;

            while (stockDataList.size() > i && Integer.valueOf(stockDataList.get(i).getDate()) < nextWeek){
                currentStock = stockDataList.get(i); // nextBar
                currentHigh = Math.max(currentHigh, currentStock.getHigh());
                currentLow = Math.min(currentLow, currentStock.getLow());
                currentVolume += currentStock.getVolume();
                i++;
            }
//            long currentEndTime = currentStock.getTimestamp();
            double currentClose = currentStock.getClose();
            aggStockData.add(new StockData("currentEndTime", "BTC", currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return aggStockData;
    }
}
