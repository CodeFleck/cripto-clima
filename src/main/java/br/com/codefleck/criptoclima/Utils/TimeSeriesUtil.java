package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.StockData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    public List<Candle> aggregateTimeSeriesToOneWeek(List<Candle> candleList) {
        List<Candle> aggCandles = new ArrayList<>();
        for (int i = 0; i < candleList.size() ; i++) {
            Candle currentCandle = candleList.get(i);
            double currentOpen = currentCandle.getOpen();
            double currentLow = currentCandle.getLow();
            double currentHigh = currentCandle.getHigh();
            double currentVolume = currentCandle.getVolume();

            LocalDateTime tempLocalDateTime = generateLocalDateTime(currentCandle);
            LocalDateTime nextWeek = tempLocalDateTime.plusDays(7);

            while (candleList.size() > i && (generateLocalDateTime(candleList.get(i)).isBefore(nextWeek))){
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

    public List<StockData> aggregateStockDataToWeek(List<StockData> stockDataList) {
        System.out.println("Aggregating StockData to one week...");
        List<StockData> weeklyStockData = new ArrayList<>();

        for (int i = 0; i < stockDataList.size() ; i++) {
            StockData currentStock = stockDataList.get(i);
            double currentOpen = currentStock.getOpen();
            double currentLow = currentStock.getLow();
            double currentHigh = currentStock.getHigh();
            double currentVolume = currentStock.getVolume();

            //get date from millis and parse it into ZoneDateTime
            ZonedDateTime date = getZonedDateTime(currentStock);

            while (stockDataList.size() > i && getZonedDateTime(stockDataList.get(i)).isBefore(date.plusDays(1))){
                currentStock = stockDataList.get(i);
                currentHigh = Math.max(currentHigh, currentStock.getHigh());
                currentLow = Math.min(currentLow, currentStock.getLow());
                currentVolume += currentStock.getVolume();
                i++;
            }
            long currentEndTime = Long.valueOf(currentStock.getDate());
            double currentClose = currentStock.getClose();
            weeklyStockData.add(new StockData(String.valueOf(currentEndTime), "BTC", currentOpen, currentClose, currentLow, currentHigh, currentVolume));
        }
        return weeklyStockData;
    }

    @NotNull
    private ZonedDateTime getZonedDateTime(StockData currentStock) {
        Date tempDate = new Date(Long.valueOf(currentStock.getDate()) * 1000L);
        final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy,MM,dd HH:mm:ss");
        String dateAsText = new SimpleDateFormat("yyyy,MM,dd HH:mm:ss").format(tempDate);
        return ZonedDateTime.of(LocalDate.parse(dateAsText, DATE_FORMAT), LocalTime.parse(dateAsText, DATE_FORMAT), ZoneId.systemDefault());
    }
}
