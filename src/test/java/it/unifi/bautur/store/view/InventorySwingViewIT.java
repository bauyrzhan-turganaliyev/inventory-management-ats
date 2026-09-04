package it.unifi.bautur.store.view;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

class InventorySwingViewIT {

	private Robot robot;
	private FrameFixture window;

	private InventorySwingView view;
	private InventoryPresenter presenter;

	@BeforeEach
	void setUp() {
		robot = BasicRobot.robotWithNewAwtHierarchy();

		presenter = mock(InventoryPresenter.class);

		view = GuiActionRunner.execute(InventorySwingView::new);

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
	void shouldDelegateAddProductToPresenter() {
		window.textBox("productNameField").enterText("Laptop");

		window.textBox("productPriceField").enterText("1200.50");

		window.button("addProductButton").click();

		verify(presenter).addProduct("Laptop", 1200.50);
	}

	@Test
	void shouldDelegateDeleteProductToPresenter() {
		Product product = new Product("Laptop", 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		window.table("productTable").selectRows(0);

		window.button("deleteProductButton").click();

		verify(presenter).deleteProduct(10L);
	}

	@Test
	void shouldDelegateAssignCategoryToPresenter() {
		Product product = new Product("Laptop", 1200.50);

		Category category = new Category("Electronics");

		setId(product, 10L);
		setId(category, 20L);

		GuiActionRunner.execute(() -> {
			view.showProducts(List.of(product));

			view.showCategories(List.of(category));
		});

		window.table("productTable").selectRows(0);

		window.comboBox("categoryComboBox").selectItem("Electronics");

		window.button("assignCategoryButton").click();

		verify(presenter).assignCategory(10L, 20L);
	}

	@Test
	void shouldDelegateRefreshToPresenter() {
		window.button("refreshButton").click();

		verify(presenter).loadProducts();

		verify(presenter).loadCategories();
	}

	@Test
	void shouldShowProductsInTable() {
		Product laptop = new Product("Laptop", 1200.50);

		Product phone = new Product("Phone", 800.00);

		setId(laptop, 10L);
		setId(phone, 20L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(laptop, phone)));

		assertThat(window.table("productTable").rowCount()).isEqualTo(2);

		window.table("productTable").requireCellValue(TableCell.row(0).column(1), "Laptop");

		window.table("productTable").requireCellValue(TableCell.row(1).column(1), "Phone");
	}

	@Test
	void shouldShowProductCategoryInTable() {
		Product product = new Product("Laptop", 1200.50);

		Category category = new Category("Electronics");

		product.setCategory(category);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		window.table("productTable").requireCellValue(TableCell.row(0).column(3), "Electronics");
	}

	@Test
	void shouldShowCategoriesInComboBox() {
		Category electronics = new Category("Electronics");

		Category books = new Category("Books");

		setId(electronics, 10L);
		setId(books, 20L);

		GuiActionRunner.execute(() -> view.showCategories(List.of(electronics, books)));

		window.comboBox("categoryComboBox").requireItemCount(2);

		window.comboBox("categoryComboBox").requireSelection("Electronics");
	}

	@Test
	void shouldShowErrorWhenDeletingWithoutSelectedProduct() {
		window.button("deleteProductButton").click();

		window.dialog().requireVisible().optionPane().requireErrorMessage().requireMessage("Please select a product")
				.okButton().click();
	}

	@Test
	void shouldShowErrorWhenAssigningCategoryWithoutSelectedProduct() {
		window.button("assignCategoryButton").click();

		window.dialog().requireVisible().optionPane().requireErrorMessage().requireMessage("Please select a product")
				.okButton().click();
	}

	@Test
	void shouldShowErrorWhenPriceIsInvalid() {
		window.textBox("productNameField").enterText("Laptop");

		window.textBox("productPriceField").enterText("not-a-number");

		window.button("addProductButton").click();

		window.dialog().requireVisible().optionPane().requireErrorMessage()
				.requireMessage("Price must be a valid number").okButton().click();
	}

	@Test
	void shouldShowErrorWhenAssigningWithoutCategory() {
		Product product = new Product("Laptop", 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		window.table("productTable").selectRows(0);

		window.button("assignCategoryButton").click();

		window.dialog().requireVisible().optionPane().requireErrorMessage().requireMessage("Please select a category")
				.okButton().click();
	}

	private static void setId(Object entity, Long id) {
		try {
			Field idField = entity.getClass().getDeclaredField("id");

			idField.setAccessible(true);
			idField.set(entity, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Cannot set entity id for GUI test", exception);
		}
	}
}