package br.com.codefleck.criptoclima.services;

import br.com.codefleck.criptoclima.enitities.results.Result;

import java.util.Optional;

public interface ResultService {

    Iterable<Result> listAllResults();

    Optional<Result> getResultById(Integer id);

    Result saveResult(Result result);

    void deleteResultById(Integer id);

    void deleteResult(Result result);

}
