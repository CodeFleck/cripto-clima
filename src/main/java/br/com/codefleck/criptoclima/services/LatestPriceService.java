package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.LatestPrice;

import java.util.List;
import java.util.Optional;

public interface LatestPriceService {

    List<LatestPrice> listAllLatestPrices();

    Optional<LatestPrice> getLatestPriceById(Integer id);

    LatestPrice saveLatestPrice(LatestPrice latestPrice);

    void deleteLatestPriceById(Integer id);

    void deleteLatestPrice(LatestPrice latestPrice);

    Optional<LatestPrice> findLastLatestPrice();

    float calculateDailyChangePercentage(double currentPrice, double futurePrice);
}
