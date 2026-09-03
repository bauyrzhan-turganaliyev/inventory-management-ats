package it.unifi.bautur.store.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.bautur.store.model.Product;
import jakarta.persistence.EntityManager;

class ProductJpaRepositoryIT extends AbstractJpaIT {

	private EntityManager entityManager;
	private ProductJpaRepository repository;

	@BeforeEach
	void setUp() {
		entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
		repository = new ProductJpaRepository(entityManager);

		entityManager.getTransaction().begin();
	}

	@AfterEach
	void tearDown() {
		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().rollback();
		}

		entityManager.close();
	}

	@Test
	void shouldSaveNewProduct() {
		Product product = new Product("Laptop", 1200.50);

		Product saved = repository.save(product);

		entityManager.flush();

		assertThat(saved).isSameAs(product);
		assertThat(saved.getId()).isNotNull();

		Product found = entityManager.find(Product.class, saved.getId());

		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Laptop");
		assertThat(found.getPrice()).isEqualTo(1200.50);
	}

	@Test
	void shouldFindProductById() {
		Product product = new Product("Laptop", 1200.50);

		entityManager.persist(product);
		entityManager.flush();
		entityManager.clear();

		assertThat(repository.findById(product.getId())).isPresent().get().satisfies(found -> {
			assertThat(found.getName()).isEqualTo("Laptop");

			assertThat(found.getPrice()).isEqualTo(1200.50);
		});
	}

	@Test
	void shouldReturnEmptyWhenProductDoesNotExist() {
		assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
	}

	@Test
	void shouldFindAllProducts() {
		entityManager.persist(new Product("Laptop", 1200.50));

		entityManager.persist(new Product("Phone", 800.00));

		entityManager.flush();
		entityManager.clear();

		List<Product> products = repository.findAll();

		assertThat(products).extracting(Product::getName).containsExactlyInAnyOrder("Laptop", "Phone");
	}

	@Test
	void shouldUpdateExistingProduct() {
		Product product = new Product("Laptop", 1200.50);

		entityManager.persist(product);
		entityManager.flush();

		Long id = product.getId();

		entityManager.detach(product);

		Product merged = repository.save(product);

		entityManager.flush();
		entityManager.clear();

		assertThat(merged).isNotSameAs(product);
		assertThat(merged.getId()).isEqualTo(id);

		Product found = entityManager.find(Product.class, id);

		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Laptop");
	}

	@Test
	void shouldDeleteExistingProduct() {
		Product product = new Product("Laptop", 1200.50);

		entityManager.persist(product);
		entityManager.flush();

		Long id = product.getId();

		repository.deleteById(id);

		entityManager.flush();
		entityManager.clear();

		assertThat(entityManager.find(Product.class, id)).isNull();
	}

	@Test
	void shouldDoNothingWhenDeletingNonExistingProduct() {
		repository.deleteById(Long.MAX_VALUE);

		entityManager.flush();

		assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
	}
}