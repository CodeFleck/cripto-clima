package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.Utils.TimeSeriesUtil;
import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.repositories.CandleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Candle service implement.
 */
@Service
public class CandleServiceImpl implements CandleService {

    private CandleRepository candleRepository;
    private TimeSeriesUtil timeSeriesUtil = new TimeSeriesUtil();

    @Autowired
    public void setCandleRepository(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    @Override
    public List<Candle> listAllCandles() {
        Iterable<Candle> iterable = candleRepository.findAll();
        List<Candle> candleList = new ArrayList<>();
        iterable.forEach(candleList::add);

        return candleList;
    }

    @Override
    public Optional<Candle> getCandleById(Integer id) {
        return candleRepository.findById(id);
    }

    @Override
    public Candle saveCandle(Candle candle) {
        return candleRepository.save(candle);
    }

    @Override
    public void deleteCandleById(Integer id) {
        candleRepository.deleteById(id);
    }

    @Override
    public void deleteCandle(Candle candle) {
        candleRepository.delete(candle);
    }

    @Override
    public List<Candle> findLastHourCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(1, ChronoUnit.HOURS);
        long timestampMillisMinusOneHour = (instant.toEpochMilli());
        List<Candle> oneMinuteAggregatedList = timeSeriesUtil.aggregateTimeSeriesToOneMinute(candleRepository.findByTimestamp(timestampMillisMinusOneHour));

        return oneMinuteAggregatedList;
    }

    @Override
    public List<Candle> findLast30DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(30, ChronoUnit.DAYS);
        long timestampMillisMinus30Days = (instant.toEpochMilli());
        List<Candle> oneDayAggregatedList = timeSeriesUtil.aggregateTimeSeriesToOneDay(candleRepository.findByTimestamp(timestampMillisMinus30Days));

        return oneDayAggregatedList;
    }

    @Override
    public List<Candle> findLast60DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(60, ChronoUnit.DAYS);
        long timestampMillisMinus60Days = (instant.toEpochMilli());
        List<Candle> twoDaysAggregatedList = timeSeriesUtil.aggregateTimeSeriesToTwoDays(candleRepository.findByTimestamp(timestampMillisMinus60Days));

        return twoDaysAggregatedList;
    }

    @Override
    public List<Candle> findLast90DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(90, ChronoUnit.DAYS);
        long timestampMillisMinus90Days = (instant.toEpochMilli());
        List<Candle> threeDaysAggregatedList = timeSeriesUtil.aggregateTimeSeriesToThreeDays(candleRepository.findByTimestamp(timestampMillisMinus90Days));

        return threeDaysAggregatedList;
    }

    @Override
    public List<Candle> findLast120DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(120, ChronoUnit.DAYS);
        long timestampMillisMinus120Days = (instant.toEpochMilli());
        List<Candle> fourDaysAggregatedList = timeSeriesUtil.aggregateTimeSeriesToFourDays(candleRepository.findByTimestamp(timestampMillisMinus120Days));

        return fourDaysAggregatedList;
    }

    @Override
    public List<Candle> findLast190DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(190, ChronoUnit.DAYS);
        long timestampMillisMinus190Days = (instant.toEpochMilli());
        List<Candle> fiveDaysAggregatedList = timeSeriesUtil.aggregateTimeSeriesToFiveDays(candleRepository.findByTimestamp(timestampMillisMinus190Days));

        return fiveDaysAggregatedList;
    }

    @Override
    public List<Candle> findLast210DaysCandles() {
        Instant instant = Instant.now();
        instant = instant.minus(210, ChronoUnit.DAYS);
        long timestampMillisMinus210Days = (instant.toEpochMilli());
        List<Candle> oneWeekAggregatedList = timeSeriesUtil.aggregateTimeSeriesToSixDays(candleRepository.findByTimestamp(timestampMillisMinus210Days));

        return oneWeekAggregatedList;

    }

}
