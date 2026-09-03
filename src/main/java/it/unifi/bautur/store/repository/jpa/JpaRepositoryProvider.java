package it.unifi.bautur.store.repository.jpa;

import it.unifi.bautur.store.repository.CategoryRepository;
import it.unifi.bautur.store.repository.ProductRepository;
import it.unifi.bautur.store.repository.RepositoryProvider;
import jakarta.persistence.EntityManager;

public class JpaRepositoryProvider implements RepositoryProvider {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public JpaRepositoryProvider(EntityManager entityManager) {
		this.productRepository = new ProductJpaRepository(entityManager);

		this.categoryRepository = new CategoryJpaRepository(entityManager);
	}

	@Override
	public ProductRepository getProductRepository() {
		return productRepository;
	}

	@Override
	public CategoryRepository getCategoryRepository() {
		return categoryRepository;
	}
}