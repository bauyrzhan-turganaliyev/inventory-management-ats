package it.unifi.bautur.store.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.unifi.bautur.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class JpaTransactionManagerIT {

	@Container
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

	private static EntityManagerFactory entityManagerFactory;
	private static JpaTransactionManager transactionManager;

	@BeforeAll
	static void setUp() {
		Map<String, Object> properties = Map.of("jakarta.persistence.jdbc.url", POSTGRES.getJdbcUrl(),
				"jakarta.persistence.jdbc.user", POSTGRES.getUsername(), "jakarta.persistence.jdbc.password",
				POSTGRES.getPassword(), "jakarta.persistence.jdbc.driver", "org.postgresql.Driver",
				"hibernate.hbm2ddl.auto", "create-drop");

		entityManagerFactory = Persistence.createEntityManagerFactory("inventory-pu", properties);

		transactionManager = new JpaTransactionManager(entityManagerFactory);
	}

	@AfterAll
	static void tearDown() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	@Test
	void shouldCommitTransactionAndReturnResult() {
		Long productId = transactionManager.doInTransaction(provider -> {
			Product product = new Product("Laptop", 1200.50);

			Product saved = provider.getProductRepository().save(product);

			return saved.getId();
		});

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		try {
			Product found = entityManager.find(Product.class, productId);

			assertThat(found).isNotNull();
			assertThat(found.getName()).isEqualTo("Laptop");
		} finally {
			entityManager.close();
		}
	}

	@Test
	void shouldRollbackTransactionWhenWorkThrowsException() {
		assertThatThrownBy(() -> transactionManager.doInTransaction(provider -> {
			provider.getProductRepository().save(new Product("Should Rollback", 100.00));

			throw new IllegalStateException("Something went wrong");
		})).isInstanceOf(IllegalStateException.class).hasMessage("Something went wrong");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		try {
			Long count = entityManager.createQuery("""
					SELECT COUNT(p)
					FROM Product p
					WHERE p.name = :name
					""", Long.class).setParameter("name", "Should Rollback").getSingleResult();

			assertThat(count).isZero();
		} finally {
			entityManager.close();
		}
	}

	@Test
	void shouldProvideBothRepositoriesInTransaction() {
		transactionManager.doInTransaction(provider -> {
			assertThat(provider.getProductRepository()).isInstanceOf(ProductJpaRepository.class);

			assertThat(provider.getCategoryRepository()).isInstanceOf(CategoryJpaRepository.class);

			return null;
		});
	}
}