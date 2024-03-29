package br.com.codefleck.criptoclima.enitities.results;

import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PriceCategory priceCategory;
    private double prediction;
    private double actual;
    private TimePeriod TimePeriod;
    private int resultSetID;
    private int resultsForWeekDay;

    public Result() {
    }

    public Result(PriceCategory priceCategory, double prediction, double actual, TimePeriod timePeriod, int resultSetID, int resultsForWeekDay) {
        this.priceCategory = priceCategory;
        this.prediction = prediction;
        this.actual = actual;
        TimePeriod = timePeriod;
        this.resultSetID = resultSetID;
        this.resultsForWeekDay = resultsForWeekDay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PriceCategory getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(PriceCategory priceCategory) {
        this.priceCategory = priceCategory;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }

    public TimePeriod getTimePeriod() {
        return TimePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        TimePeriod = timePeriod;
    }

    public int getResultSetID() {
        return resultSetID;
    }

    public void setResultSetID(int resultSetID) {
        this.resultSetID = resultSetID;
    }

    public int getResultsForWeekDay() {
        return resultsForWeekDay;
    }

    public void setResultsForWeekDay(int resultsForWeekDay) {
        this.resultsForWeekDay = resultsForWeekDay;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", priceCategory=" + priceCategory +
                ", prediction=" + prediction +
                ", actual=" + actual +
                ", TimePeriod=" + TimePeriod +
                ", resultSetID=" + resultSetID +
                ", resultsForWeekDay=" + resultsForWeekDay +
                '}';
    }
}
