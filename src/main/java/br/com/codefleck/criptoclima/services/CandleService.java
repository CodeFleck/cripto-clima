package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.Candle;

import java.util.List;
import java.util.Optional;

public interface CandleService {

    List<Candle> listAllCandles();

    Optional<Candle> getCandleById(Integer id);

    Candle saveCandle(Candle candle);

    void deleteCandleById(Integer id);

    void deleteCandle(Candle candle);

    List<Candle> findLastHourCandles();

    List<Candle> findLast24HoursCandles();

    List<Candle> findLast30DaysCandles();

    List<Candle> findLast60DaysCandles();

    List<Candle> findLast210DaysCandles();

    List<Candle> findLast90DaysCandles();

    List<Candle> findLast120DaysCandles();

    List<Candle> findLast190DaysCandles();

    Optional<Candle> findLastCandle();
}
