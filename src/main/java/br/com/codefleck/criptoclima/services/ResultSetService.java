package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface ResultSetService {

    Iterable<ResultSet> listAllResultSets();

    Optional<ResultSet> getResultSetById(Integer id);

    ResultSet saveResultSet(ResultSet resultSet);

    void deleteResultSetById(Integer id);

    void deleteResultSet(ResultSet resultSet);

    Optional<ResultSet> findFirstDailyResultSetByOrderByIdDesc();

    Optional<ResultSet> findLatestWeeklyResultSet();

    Optional<ResultSet> findLatestDailyResultSet();

}
