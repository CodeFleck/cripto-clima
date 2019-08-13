package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.Candle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CandleRepository extends CrudRepository<Candle, Integer> {

    @Query("Select c from Candle c where c.timestamp > ?1")
    List<Candle> findByTimestamp(long timeStampMillisMinusOneHour);
}
