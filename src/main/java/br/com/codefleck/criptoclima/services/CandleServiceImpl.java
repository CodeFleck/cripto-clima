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
    private TimeSeriesUtil timeSeriesUtil;

    @Autowired
    public void setTimeSeriesUtil(TimeSeriesUtil timeSeriesUtil) { this.timeSeriesUtil = timeSeriesUtil; }

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
    public List<Candle> findLast75DaysCandles() {

        Instant instant = Instant.now();
        instant = instant.minus(75, ChronoUnit.DAYS);
        long timestampMillisMinus75Days = (instant.toEpochMilli());
        List<Candle> oneHourAggregatedList = timeSeriesUtil.aggregateTimeSeriesToOneHour(candleRepository.findByTimestamp(timestampMillisMinus75Days));

        return oneHourAggregatedList;
    }
}
