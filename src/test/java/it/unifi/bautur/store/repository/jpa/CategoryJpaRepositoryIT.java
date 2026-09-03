package it.unifi.bautur.store.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.bautur.store.model.Category;
import jakarta.persistence.EntityManager;

class CategoryJpaRepositoryIT extends AbstractJpaIT {

	private EntityManager entityManager;
	private CategoryJpaRepository repository;

	@BeforeEach
	void setUp() {
		entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
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