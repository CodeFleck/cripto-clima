package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface ResultSetRepository extends CrudRepository<ResultSet, Integer> {

    Optional<ResultSet> findFirstByOrderByIdDesc();

    @Query(value = "SELECT * FROM result_set ORDER BY id DESC", nativeQuery = true)
    Page<ResultSet> findAllResultSetWithPagination(PageRequest pageable);

}
