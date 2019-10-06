package br.com.codefleck.criptoclima.Utils;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.CustomStockData;
import br.com.codefleck.criptoclima.enitities.StockData;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StockDataUtil {

    public StockDataUtil() { }

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
            Timestamp timestamp = convertStringDateToTimestamp(stock.getDate());
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

    public static Timestamp convertStringDateToTimestamp(String dateAsString) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(dateAsString);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public List<CustomStockData> tranformCandleInCustomStockData(List<Candle> candleList) {

        List<CustomStockData> stockDataList = new ArrayList<>();

        for (int i=0; i< candleList.size(); i++){
            if (i == 0){
                CustomStockData stockData = new CustomStockData();
                stockData.setDate(String.valueOf(candleList.get(i).getTimestamp()));
                stockData.setSymbol("BTC");
                stockData.setOpen(candleList.get(i).getOpen());
                stockData.setClose(candleList.get(i).getClose());
                stockData.setLow(candleList.get(i).getLow());
                stockData.setHigh(candleList.get(i).getHigh());
                stockData.setVolume(candleList.get(i).getVolume());
                stockData.setDailyChangePercentage(0);
                stockDataList.add(stockData);
            } else {
                CustomStockData stockData = new CustomStockData();
                stockData.setDate(String.valueOf(candleList.get(i).getTimestamp()));
                stockData.setSymbol("BTC");
                stockData.setOpen(candleList.get(i).getOpen());
                stockData.setClose(candleList.get(i).getClose());
                stockData.setLow(candleList.get(i).getLow());
                stockData.setHigh(candleList.get(i).getHigh());
                stockData.setVolume(candleList.get(i).getVolume());
                stockData.setDailyChangePercentage(round(calculatePercentage(candleList.get(i).getClose(), candleList.get(i-1).getClose())));
                stockDataList.add(stockData);
            }
        }

        return stockDataList;
    }

    public static double calculatePercentage(double currentPrice, double previousPrice) {
        double variationAmmount = (currentPrice - previousPrice);
        double result = (variationAmmount*100) / previousPrice;
        return result;
    }

    private static double round(double n){
        return Math.round(n * 10000d) / 10000d;
    }
}
