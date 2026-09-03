package it.unifi.bautur.store.repository.jpa;

import java.util.List;
import java.util.Optional;

import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.repository.ProductRepository;
import jakarta.persistence.EntityManager;

public class ProductJpaRepository implements ProductRepository {

	private final EntityManager entityManager;

	public ProductJpaRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Optional<Product> findById(Long id) {
		Product product = entityManager.find(Product.class, id);

		return Optional.ofNullable(product);
	}

	@Override
	public List<Product> findAll() {
		return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
	}

	@Override
	public Product save(Product product) {
		if (product.getId() == null) {
			entityManager.persist(product);
			return product;
		}

		return entityManager.merge(product);
	}

	@Override
	public void deleteById(Long id) {
		Product product = entityManager.find(Product.class, id);

		if (product != null) {
			entityManager.remove(product);
		}
	}
}