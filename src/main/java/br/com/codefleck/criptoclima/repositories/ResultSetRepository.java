package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ResultSetRepository extends CrudRepository<ResultSet, Integer> {

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 0 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithOneDayPeriod();

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 1 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithTwoDaysPeriod();

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 2 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithThreeDaysPeriod();

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 3 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithFourDaysPeriod();

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 4 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithFiveDaysPeriod();

    @Query(value = "SELECT * FROM result_set u WHERE u.period = 5 ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Optional<ResultSet> findLatestResultSetWithSixDaysPeriod();
}
