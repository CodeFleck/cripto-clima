package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.LatestPrice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LatestPriceRepository extends CrudRepository<LatestPrice, Integer> {

    Optional<LatestPrice> findFirstByOrderByIdDesc();
}
