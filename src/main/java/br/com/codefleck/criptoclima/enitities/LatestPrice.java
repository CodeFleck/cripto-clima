package br.com.codefleck.criptoclima.enitities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LatestPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String timestamp;
    private String coin;
    private double latestPrice;

    public LatestPrice() {
    }

    public LatestPrice(String timestamp, String coin, double latestPrice) {
        this.timestamp = timestamp;
        this.coin = coin;
        this.latestPrice = latestPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    @Override
    public String toString() {
        return "LatestPrice{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", coin=" + coin +
                ", latestPrice=" + latestPrice +
                '}';
    }
}
