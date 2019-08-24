package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.repositories.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Result service implement.
 */
@Service
public class ResultServiceImpl implements ResultService {

    private ResultRepository resultRepository;

    @Autowired
    public void setResultRepository(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Iterable<Result> listAllResults() {
        return resultRepository.findAll();
    }

    @Override
    public Optional<Result> getResultById(Integer id) {
        return resultRepository.findById(id);
    }

    @Override
    public Result saveResult(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public void deleteResultById(Integer id) {
        resultRepository.deleteById(id);
    }

    @Override
    public void deleteResult(Result result) {
        resultRepository.delete(result);
    }

    @Override
    public Optional<List<Result>> getResultListByResultSetId(Integer id) {
        return resultRepository.findAllByResultSetID(id);
    }
}
