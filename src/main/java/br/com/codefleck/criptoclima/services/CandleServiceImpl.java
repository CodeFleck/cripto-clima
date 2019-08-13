package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.repositories.CandleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Candle service implement.
 */
@Service
public class CandleServiceImpl implements CandleService {

    private CandleRepository candleRepository;

    @Autowired
    public void setCandleRepository(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    @Override
    public Iterable<Candle> listAllCandles() {
        return candleRepository.findAll();
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
        long timestampMillisMinusOneHour = (instant.toEpochMilli() - 3600000);

        return candleRepository.findByTimestamp(timestampMillisMinusOneHour);
    }
}
