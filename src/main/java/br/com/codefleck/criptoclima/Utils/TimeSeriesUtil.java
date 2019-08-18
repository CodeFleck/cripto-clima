package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
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
            double currentHigh = currentCandle.getHigh();
            double currentLow = currentCandle.getLow();
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
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentHigh, currentLow, currentVolume));
        }
        return aggCandles;
    }

    public List<Candle> aggregateTimeSeriesToOneHour(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentHigh = currentCandle.getHigh();
            double currentLow = currentCandle.getLow();
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
            aggCandles.add(new Candle(currentEndTime, currentOpen, currentClose, currentHigh, currentLow, currentVolume));
        }
        return aggCandles;
    }
}
