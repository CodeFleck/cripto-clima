package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;

import java.util.Optional;

public interface ResultSetService {

    Iterable<ResultSet> listAllResultSets();

    Optional<ResultSet> getResultSetById(Integer id);

    ResultSet saveResultSet(ResultSet resultSet);

    void deleteResultSetById(Integer id);

    void deleteResultSet(ResultSet resultSet);

    ResultSet findFirstByOrderByIdDesc();

}
