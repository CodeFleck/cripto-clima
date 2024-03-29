package br.com.codefleck.criptoclima.enitities;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Candle entity.
 */
@Entity
public class Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private long timestamp;
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;

    public Candle() {
    }

    public Candle(long timestamp, double open, double close, double low, double high, double volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.volume = volume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", volume=" + volume +
                '}';
    }
}
