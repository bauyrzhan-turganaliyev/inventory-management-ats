package it.unifi.bautur.store.repository;

import java.util.List;
import java.util.Optional;

import it.unifi.bautur.store.model.Product;

public interface ProductRepository {
	Optional<Product> findById(Long id);

	List<Product> findAll();

	Product save(Product product);

	void deleteById(Long id);
}