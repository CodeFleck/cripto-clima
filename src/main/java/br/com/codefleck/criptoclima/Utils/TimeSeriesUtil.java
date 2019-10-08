package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesUtil {

    public TimeSeriesUtil() { }

    public List<Candle> aggregateTimeSeriesToOneMinute(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextMinute = tempLocalDateTime.plusMinutes(1);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextMinute)){
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

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextHour = tempLocalDateTime.plusHours(1);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextHour)){
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

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nexDay = tempLocalDateTime.plusDays(1);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nexDay)){
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

    public List<Candle> aggregateTimeSeriesToTwoDays(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextTwoDays = tempLocalDateTime.plusDays(2);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextTwoDays)){
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

    public List<Candle> aggregateTimeSeriesToThreeDays(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextThreeDays = tempLocalDateTime.plusDays(3);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextThreeDays)){
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

    public List<Candle> aggregateTimeSeriesToFourDays(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextFourDays = tempLocalDateTime.plusDays(4);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextFourDays)){
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

    public List<Candle> aggregateTimeSeriesToFiveDays(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextFiveDays = tempLocalDateTime.plusDays(5);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextFiveDays)){
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

    public List<Candle> aggregateTimeSeriesToSixDays(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextSixDays = tempLocalDateTime.plusDays(6);

            while (candleList.size() > i && generateLocalDateTime(candleList.get(i)).isBefore(nextSixDays)){
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

    private LocalDateTime generateLocalDateTime(Candle currentCandle) {
        Timestamp ts = new Timestamp(currentCandle.getTimestamp());
        return ts.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
