package it.unifi.bautur.store;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.repository.jpa.JpaTransactionManager;
import it.unifi.bautur.store.service.InventoryService;
import it.unifi.bautur.store.view.InventoryPresenter;
import it.unifi.bautur.store.view.InventorySwingView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public final class Main {

	private static final String PERSISTENCE_UNIT = "inventory-pu";

	private Main() {
	}

	public static void main(String[] args) {

		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();

		Runtime.getRuntime().addShutdownHook(new Thread(entityManagerFactory::close));

		initializeCategories(entityManagerFactory);

		SwingUtilities.invokeLater(() -> createAndShowGui(entityManagerFactory));
	}

	private static EntityManagerFactory createEntityManagerFactory() {

		Map<String, Object> properties = new HashMap<>();

		putEnvironmentProperty(properties, "DB_URL", "jakarta.persistence.jdbc.url");

		putEnvironmentProperty(properties, "DB_USER", "jakarta.persistence.jdbc.user");

		putEnvironmentProperty(properties, "DB_PASSWORD", "jakarta.persistence.jdbc.password");

		properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

		properties.put("hibernate.hbm2ddl.auto", "update");

		return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
	}

	private static void putEnvironmentProperty(Map<String, Object> properties, String environmentVariable,
			String persistenceProperty) {

		String value = System.getenv(environmentVariable);

		if (value != null && !value.isBlank()) {
			properties.put(persistenceProperty, value);
		}
	}

	private static void initializeCategories(EntityManagerFactory entityManagerFactory) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		try {

			Long categoryCount = entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
					.getSingleResult();

			if (categoryCount == 0) {

				transaction.begin();

				entityManager.persist(new Category("Electronics"));

				entityManager.persist(new Category("Books"));

				entityManager.persist(new Category("Food"));

				transaction.commit();
			}

		} catch (RuntimeException exception) {

			if (transaction.isActive()) {
				transaction.rollback();
			}

			throw exception;

		} finally {
			entityManager.close();
		}
	}

	private static void createAndShowGui(EntityManagerFactory entityManagerFactory) {

		JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);

		InventoryService service = new InventoryService(transactionManager);

		InventorySwingView view = new InventorySwingView();

		InventoryPresenter presenter = new InventoryPresenter(view, service);

		view.setPresenter(presenter);
		view.setVisible(true);

		presenter.loadCategories();
		presenter.loadProducts();
	}
}	