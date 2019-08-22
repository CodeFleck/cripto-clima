package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.repositories.ResultSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ResultSet service implement.
 */
@Service
public class ResultSetServiceImpl implements ResultSetService {

    private ResultSetRepository resultSetRepository;

    @Autowired
    public void setResultSetRepository(ResultSetRepository resultSetRepository) {
        this.resultSetRepository = resultSetRepository;
    }

    @Override
    public Iterable<ResultSet> listAllResultSets() {
        return resultSetRepository.findAll();
    }

    @Override
    public Optional<ResultSet> getResultSetById(Integer id) {
        return resultSetRepository.findById(id);
    }

    @Override
    public ResultSet saveResultSet(ResultSet resultSet) {
        return resultSetRepository.save(resultSet);
    }

    @Override
    public void deleteResultSetById(Integer id) {
        resultSetRepository.deleteById(id);
    }

    @Override
    public void deleteResultSet(ResultSet resultSet) {
        resultSetRepository.delete(resultSet);
    }

    @Override
    public ResultSet findFirstByOrderByIdDesc() {
        return resultSetRepository.findFirstByOrderByIdDesc();
    }
}
