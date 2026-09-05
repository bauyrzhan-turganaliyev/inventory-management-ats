package it.unifi.bautur.store.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.repository.jpa.AbstractJpaIT;
import it.unifi.bautur.store.repository.jpa.JpaTransactionManager;
import it.unifi.bautur.store.service.InventoryService;
import jakarta.persistence.EntityManager;

class InventoryE2EIT extends AbstractJpaIT {

	private Robot robot;

	private FrameFixture window;

	private InventorySwingView view;

	private InventoryPresenter presenter;

	private JpaTransactionManager transactionManager;

	@BeforeEach
	void setUp() {
		cleanDatabase();

		transactionManager = new JpaTransactionManager(ENTITY_MANAGER_FACTORY);

		InventoryService service = new InventoryService(transactionManager);

		robot = BasicRobot.robotWithNewAwtHierarchy();

		view = GuiActionRunner.execute(InventorySwingView::new);

		presenter = new InventoryPresenter(view, service);

		GuiActionRunner.execute(() -> view.setPresenter(presenter));

		window = new FrameFixture(robot, view);

		window.show();
	}

	@AfterEach
	void tearDown() {
		if (window != null) {
			window.cleanUp();
		}
	}

	@Test
	void shouldAddProductEndToEnd() {
		window.textBox("productNameField").enterText("Laptop");

		window.textBox("productPriceField").enterText("1200.50");

		window.button("addProductButton").click();

		window.table("productTable").requireRowCount(1);

		window.table("productTable").requireCellValue(TableCell.row(0).column(1), "Laptop");

		window.table("productTable").requireCellValue(TableCell.row(0).column(2), "1200.5");

		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			List<Product> products = entityManager.createQuery("SELECT p FROM Product p", Product.class)
					.getResultList();

			assertThat(products).hasSize(1);

			Product product = products.get(0);

			assertThat(product.getId()).isNotNull();

			assertThat(product.getName()).isEqualTo("Laptop");

			assertThat(product.getPrice()).isEqualTo(1200.50);
		} finally {
			entityManager.close();
		}
	}

	@Test
	void shouldDeleteProductEndToEnd() {
		Product product = new Product("Laptop", 1200.50);

		transactionManager.doInTransaction(repositories -> {
			repositories.getProductRepository().save(product);

			return null;
		});

		assertThat(product.getId()).isNotNull();

		GuiActionRunner.execute(() -> presenter.loadProducts());

		window.table("productTable").requireRowCount(1);

		window.table("productTable").requireCellValue(TableCell.row(0).column(1), "Laptop");

		GuiActionRunner.execute(() -> window.table("productTable").target().setRowSelectionInterval(0, 0));

		window.button("deleteProductButton").click();

		window.table("productTable").requireRowCount(0);

		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			Long productCount = entityManager.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
					.getSingleResult();

			assertThat(productCount).isZero();
		} finally {
			entityManager.close();
		}
	}

	@Test
	void shouldAssignCategoryToProductEndToEnd() {

		Product product = new Product("Laptop", 1200.50);

		Category category = new Category("Electronics");

		EntityManager setupEntityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			setupEntityManager.getTransaction().begin();

			setupEntityManager.persist(category);

			setupEntityManager.getTransaction().commit();
		} catch (RuntimeException exception) {
			if (setupEntityManager.getTransaction().isActive()) {

				setupEntityManager.getTransaction().rollback();
			}

			throw exception;
		} finally {
			setupEntityManager.close();
		}

		transactionManager.doInTransaction(repositories -> {
			repositories.getProductRepository().save(product);

			return null;
		});

		assertThat(product.getId()).isNotNull();

		assertThat(category.getId()).isNotNull();

		GuiActionRunner.execute(() -> {
			presenter.loadProducts();
			presenter.loadCategories();
		});

		window.table("productTable").requireRowCount(1);

		window.table("productTable").requireCellValue(TableCell.row(0).column(1), "Laptop");

		window.comboBox("categoryComboBox").requireSelection("Electronics");

		GuiActionRunner.execute(() -> window.table("productTable").target().setRowSelectionInterval(0, 0));

		window.comboBox("categoryComboBox").selectItem("Electronics");

		window.button("assignCategoryButton").click();

		window.table("productTable").requireCellValue(TableCell.row(0).column(3), "Electronics");

		EntityManager verificationEntityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			Product storedProduct = verificationEntityManager.find(Product.class, product.getId());

			assertThat(storedProduct).isNotNull();

			assertThat(storedProduct.getCategory()).isNotNull();

			assertThat(storedProduct.getCategory().getId()).isEqualTo(category.getId());

			assertThat(storedProduct.getCategory().getName()).isEqualTo("Electronics");
		} finally {
			verificationEntityManager.close();
		}
	}

	@Test
	void shouldDisplayExistingProductsEndToEnd() {
		Product laptop = new Product("Laptop", 1200.50);

		Product phone = new Product("Phone", 800.00);

		transactionManager.doInTransaction(repositories -> {
			repositories.getProductRepository().save(laptop);

			repositories.getProductRepository().save(phone);

			return null;
		});

		GuiActionRunner.execute(() -> presenter.loadProducts());

		window.table("productTable").requireRowCount(2);

		window.table("productTable").requireCellValue(TableCell.row(0).column(1), "Laptop");

		window.table("productTable").requireCellValue(TableCell.row(1).column(1), "Phone");
	}

	private void cleanDatabase() {
		EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

		try {
			entityManager.getTransaction().begin();

			entityManager.createQuery("DELETE FROM Product").executeUpdate();

			entityManager.createQuery("DELETE FROM Category").executeUpdate();

			entityManager.getTransaction().commit();
		} catch (RuntimeException exception) {
			if (entityManager.getTransaction().isActive()) {

				entityManager.getTransaction().rollback();
			}

			throw exception;
		} finally {
			entityManager.close();
		}
	}
}