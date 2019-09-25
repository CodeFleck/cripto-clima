package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.LatestPrice;
import br.com.codefleck.criptoclima.repositories.LatestPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LatestPriceServiceImpl implements LatestPriceService {

    private LatestPriceRepository latestPriceRepository;

    @Autowired
    public void setLatestPriceRepository(LatestPriceRepository latestPriceRepository) {
        this.latestPriceRepository = latestPriceRepository;
    }

    @Override
    public List<LatestPrice> listAllLatestPrices() {
        Iterable<LatestPrice> iterable = latestPriceRepository.findAll();
        List<LatestPrice> latestPriceList = new ArrayList<>();
        iterable.forEach(latestPriceList::add);

        return latestPriceList;
    }

    @Override
    public Optional<LatestPrice> getLatestPriceById(Integer id) {
        return latestPriceRepository.findById(id);
    }

    @Override
    public LatestPrice saveLatestPrice(LatestPrice latestPrice) {
        return latestPriceRepository.save(latestPrice);
    }

    @Override
    public void deleteLatestPriceById(Integer id) {
        latestPriceRepository.deleteById(id);
    }

    @Override
    public void deleteLatestPrice(LatestPrice latestPrice) {
        latestPriceRepository.delete(latestPrice);
    }

    @Override
    public Optional<LatestPrice> findLastLatestPrice() {
        return latestPriceRepository.findFirstByOrderByIdDesc();
    }

    @Override
    public float calculateDailyChangePercentage(double currentPrice, double futurePrice) {
        double result = (currentPrice * 100) / futurePrice;
        return (float) ((result-100)*-1);
    }
}
