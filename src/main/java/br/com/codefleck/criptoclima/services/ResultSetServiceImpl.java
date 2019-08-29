package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.repositories.ResultSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
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
    public Optional<ResultSet> findFirstDailyResultSetByOrderByIdDesc() {
        return resultSetRepository.findFirstByOrderByIdDesc();
    }

    @Override
    public Optional<ResultSet> findLatestWeeklyResultSet() {
        Page<ResultSet> page = resultSetRepository.findAllResultSetWithPagination(new PageRequest(0,900));
        return page.get().filter(resultSet -> resultSet.getPeriod() == TimePeriod.ONE_WEEK).findFirst();
    }

    @Override
    public Optional<ResultSet> findLatestDailyResultSet() {
        Page<ResultSet> page = resultSetRepository.findAllResultSetWithPagination(new PageRequest(0,900));
        return page.get().filter(resultSet -> resultSet.getPeriod() == TimePeriod.ONE_DAY).findFirst();
    }
}
