package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResultSetRepository extends CrudRepository<ResultSet, Integer> {

    ResultSet findFirstByOrderByIdDesc();

}
