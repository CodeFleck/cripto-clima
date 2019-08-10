package br.com.codefleck.criptoclima.repositories;

import br.com.codefleck.criptoclima.enitities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

}
