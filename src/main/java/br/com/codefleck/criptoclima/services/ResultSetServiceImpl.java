package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.repositories.ResultSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ResultSet service implement.
 */
@Service
public class ResultSetServiceImpl implements ResultSetService {

    private ResultSetRepository resultSetRepository;

    @Override
    public ResultSet saveResultSet(ResultSet resultSet) {
        return resultSetRepository.save(resultSet);
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithOneDayPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithOneDayPeriod();
        return resultSet;
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithTwoDaysPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithTwoDaysPeriod();
        return resultSet;
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithThreeDaysPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithThreeDaysPeriod();
        return resultSet;
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithFourDaysPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithFourDaysPeriod();
        return resultSet;
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithFiveDaysPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithFiveDaysPeriod();
        return resultSet;
    }

    @Override
    public Optional<ResultSet> findLatestResultSetWithSixDaysPeriod() {
        Optional<ResultSet> resultSet = resultSetRepository.findLatestResultSetWithSixDaysPeriod();
        return resultSet;
    }

    @Autowired
    public void setResultSetRepository(ResultSetRepository resultSetRepository) {
        this.resultSetRepository = resultSetRepository;
    }
}
