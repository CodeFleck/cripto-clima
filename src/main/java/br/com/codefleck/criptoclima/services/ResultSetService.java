package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;

import java.util.Optional;

public interface ResultSetService {

    ResultSet saveResultSet(ResultSet resultSet);

    Optional<ResultSet> findLatestResultSetWithOneDayPeriod();

    Optional<ResultSet> findLatestResultSetWithTwoDaysPeriod();

    Optional<ResultSet> findLatestResultSetWithThreeDaysPeriod();

    Optional<ResultSet> findLatestResultSetWithFourDaysPeriod();

    Optional<ResultSet> findLatestResultSetWithFiveDaysPeriod();

    Optional<ResultSet> findLatestResultSetWithSixDaysPeriod();
}
