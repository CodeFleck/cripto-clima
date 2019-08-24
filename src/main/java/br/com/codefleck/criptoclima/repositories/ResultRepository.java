package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.results.Result;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends CrudRepository<Result, Integer> {

    Optional<List<Result>> findAllByResultSetID(int id);

}
