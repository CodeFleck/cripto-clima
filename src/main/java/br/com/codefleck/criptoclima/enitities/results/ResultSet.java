package br.com.codefleck.criptoclima.enitities.results;

import br.com.codefleck.criptoclima.enitities.TimePeriod;

import javax.persistence.*;
import java.util.List;

@Entity
public class ResultSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private long timestamp;
    @ElementCollection
    private List<Result> resultList;
    private TimePeriod period;

    public ResultSet() {
    }

    public ResultSet(long timestamp, List<Result> resultList, TimePeriod period) {
        this.timestamp = timestamp;
        this.resultList = resultList;
        this.period = period;
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

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    public TimePeriod getPeriod() {
        return period;
    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "ResultSet{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", resultList=" + resultList +
                ", period=" + period +
                '}';
    }
}