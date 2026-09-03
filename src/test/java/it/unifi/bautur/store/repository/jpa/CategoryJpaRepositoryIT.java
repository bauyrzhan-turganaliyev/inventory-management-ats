package it.unifi.bautur.store.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.unifi.bautur.store.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class CategoryJpaRepositoryIT {

	@Container
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

	private static EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;
	private CategoryJpaRepository repository;

	@BeforeAll
	static void createEntityManagerFactory() {
		Map<String, Object> properties = Map.of("jakarta.persistence.jdbc.url", POSTGRES.getJdbcUrl(),
				"jakarta.persistence.jdbc.user", POSTGRES.getUsername(), "jakarta.persistence.jdbc.password",
				POSTGRES.getPassword(), "jakarta.persistence.jdbc.driver", "org.postgresql.Driver",
				"hibernate.hbm2ddl.auto", "create-drop");

		entityManagerFactory = Persistence.createEntityManagerFactory("inventory-pu", properties);
	}

	@AfterAll
	static void closeEntityManagerFactory() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	@BeforeEach
	void setUp() {
		entityManager = entityManagerFactory.createEntityManager();
		repository = new CategoryJpaRepository(entityManager);
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
	void shouldFindCategoryById() {
		Category category = new Category("Electronics");

		entityManager.persist(category);
		entityManager.flush();
		entityManager.clear();

		assertThat(repository.findById(category.getId())).isPresent().get()
				.satisfies(found -> assertThat(found.getName()).isEqualTo("Electronics"));
	}

	@Test
	void shouldReturnEmptyWhenCategoryDoesNotExist() {
		assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
	}

	@Test
	void shouldFindAllCategories() {
		entityManager.persist(new Category("Electronics"));

		entityManager.persist(new Category("Books"));

		entityManager.flush();
		entityManager.clear();

		List<Category> categories = repository.findAll();

		assertThat(categories).extracting(Category::getName).containsExactlyInAnyOrder("Electronics", "Books");
	}
}