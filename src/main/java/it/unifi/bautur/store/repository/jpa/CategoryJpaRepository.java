package it.unifi.bautur.store.repository.jpa;

import java.util.List;
import java.util.Optional;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.repository.CategoryRepository;
import jakarta.persistence.EntityManager;

public class CategoryJpaRepository implements CategoryRepository {

	private final EntityManager entityManager;

	public CategoryJpaRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Optional<Category> findById(Long id) {
		Category category = entityManager.find(Category.class, id);
		return Optional.ofNullable(category);
	}

	@Override
	public List<Category> findAll() {
		return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
	}
}