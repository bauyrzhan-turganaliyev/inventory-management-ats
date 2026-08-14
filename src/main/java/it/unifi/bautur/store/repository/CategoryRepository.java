package it.unifi.bautur.store.repository;

import java.util.List;
import java.util.Optional;

import it.unifi.bautur.store.model.Category;

public interface CategoryRepository {
	Optional<Category> findById(Long id);
	List<Category> findAll();
}