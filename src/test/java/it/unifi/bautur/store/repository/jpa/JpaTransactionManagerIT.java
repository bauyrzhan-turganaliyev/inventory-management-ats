package it.unifi.bautur.store.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.unifi.bautur.store.model.Product;
import jakarta.persistence.EntityManager;

class JpaTransactionManagerIT extends AbstractJpaIT {

	private static JpaTransactionManager transactionManager;

	@BeforeAll
	static void createTransactionManager() {
		transactionManager = new JpaTransactionManager(ENTITY_MANAGER_FACTORY);
	}

	@AfterEach
	void cleanDatabase() {
		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			entityManager.getTransaction().begin();

			entityManager.createQuery("DELETE FROM Product").executeUpdate();

			entityManager.createQuery("DELETE FROM Category").executeUpdate();

			entityManager.getTransaction().commit();
		} finally {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}

			entityManager.close();
		}
	}

	@Test
	void shouldCommitTransactionAndReturnResult() {
		Long productId = transactionManager.doInTransaction(provider -> {
			Product product = new Product("Laptop", 1200.50);

			Product saved = provider.getProductRepository().save(product);

			return saved.getId();
		});

		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

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

		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

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