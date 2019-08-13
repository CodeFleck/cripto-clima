package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.Candle;

import java.util.List;
import java.util.Optional;

public interface CandleService {

    Iterable<Candle> listAllCandles();

    Optional<Candle> getCandleById(Integer id);

    Candle saveCandle(Candle candle);

    void deleteCandleById(Integer id);

    void deleteCandle(Candle candle);

    public List<Candle> findLastHourCandles();
}
