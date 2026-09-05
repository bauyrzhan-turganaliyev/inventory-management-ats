package it.unifi.bautur.store.view;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.List;

import javax.swing.SwingUtilities;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.timing.Timeout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;

class InventorySwingViewIT {

	private static final Timeout DIALOG_TIMEOUT = Timeout.timeout(5000);

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

		window.textBox("productQuantityField").enterText("5");

		window.textBox("productPriceField").enterText("1200.50");

		window.button("addProductButton").click();

		verify(presenter).addProduct("Laptop", 5, 1200.50);
	}

	@Test
	void shouldDelegateDeleteProductToPresenter() {
		Product product = new Product("Laptop", 5, 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		selectProductRow(0);

		window.button("deleteProductButton").click();

		verify(presenter).deleteProduct(10L);
	}

	@Test
	void shouldDelegateUpdateStockToPresenter() {
		Product product = new Product("Laptop", 5, 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		selectProductRow(0);

		window.textBox("stockQuantityField").enterText("20");

		window.button("updateStockButton").click();

		verify(presenter).updateProductStock(10L, 20);
	}

	@Test
	void shouldDelegateAssignCategoryToPresenter() {
		Product product = new Product("Laptop", 5, 1200.50);

		Category category = new Category("Electronics");

		setId(product, 10L);
		setId(category, 20L);

		GuiActionRunner.execute(() -> {
			view.showProducts(List.of(product));

			view.showCategories(List.of(category));
		});

		selectProductRow(0);

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
		Product laptop = new Product("Laptop", 5, 1200.50);

		Product phone = new Product("Phone", 10, 800.00);

		setId(laptop, 10L);
		setId(phone, 20L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(laptop, phone)));

		assertThat(window.table("productTable").rowCount()).isEqualTo(2);

		assertThat(window.table("productTable").valueAt(TableCell.row(0).column(1))).isEqualTo("Laptop");

		assertThat(window.table("productTable").valueAt(TableCell.row(1).column(1))).isEqualTo("Phone");
	}

	@Test
	void shouldShowProductQuantityInTable() {
		Product product = new Product("Laptop", 7, 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		assertThat(window.table("productTable").valueAt(TableCell.row(0).column(2))).isEqualTo("7");
	}

	@Test
	void shouldShowProductCategoryInTable() {
		Product product = new Product("Laptop", 5, 1200.50);

		Category category = new Category("Electronics");

		product.setCategory(category);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		assertThat(window.table("productTable").valueAt(TableCell.row(0).column(4))).isEqualTo("Electronics");
	}

	@Test
	void shouldShowCategoriesInComboBox() {
		Category electronics = new Category("Electronics");

		Category books = new Category("Books");

		setId(electronics, 10L);
		setId(books, 20L);

		GuiActionRunner.execute(() -> view.showCategories(List.of(electronics, books)));

		assertThat(window.comboBox("categoryComboBox").contents()).containsExactly("Electronics", "Books");
	}

	@Test
	void shouldShowErrorWhenStockQuantityIsInvalid() {
		Product product = new Product("Laptop", 5, 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> {
			view.showProducts(List.of(product));

			window.table("productTable").target().setRowSelectionInterval(0, 0);

			window.textBox("stockQuantityField").target().setText("not-a-number");
		});

		clickAsync("updateStockButton");

		var dialog = window.dialog(DIALOG_TIMEOUT).requireVisible();

		assertThat(dialog.optionPane().target().getMessage()).isEqualTo("Stock quantity must be a valid number");

		dialog.optionPane().okButton().click();
	}

	@ParameterizedTest
	@ValueSource(strings = { "deleteProductButton", "updateStockButton", "assignCategoryButton" })
	void shouldShowErrorWhenActionRequiresSelectedProduct(String buttonName) {

		clickAsync(buttonName);

		var dialog = window.dialog(DIALOG_TIMEOUT).requireVisible();

		assertThat(dialog.optionPane().target().getMessage()).isEqualTo("Please select a product");

		dialog.optionPane().okButton().click();
	}

	@Test
	void shouldShowErrorWhenProductNumbersAreInvalid() {
		GuiActionRunner.execute(() -> {
			window.textBox("productNameField").target().setText("Laptop");

			window.textBox("productQuantityField").target().setText("not-a-number");

			window.textBox("productPriceField").target().setText("1200.50");
		});

		clickAsync("addProductButton");

		var dialog = window.dialog(DIALOG_TIMEOUT).requireVisible();

		assertThat(dialog.optionPane().target().getMessage()).isEqualTo("Quantity and price must be valid numbers");

		dialog.optionPane().okButton().click();
	}

	@Test
	void shouldShowErrorWhenPriceIsInvalid() {
		GuiActionRunner.execute(() -> {
			window.textBox("productNameField").target().setText("Laptop");

			window.textBox("productQuantityField").target().setText("5");

			window.textBox("productPriceField").target().setText("not-a-number");
		});

		clickAsync("addProductButton");

		var dialog = window.dialog(DIALOG_TIMEOUT).requireVisible();

		assertThat(dialog.optionPane().target().getMessage()).isEqualTo("Quantity and price must be valid numbers");

		dialog.optionPane().okButton().click();
	}

	@Test
	void shouldShowErrorWhenAssigningWithoutCategory() {
		Product product = new Product("Laptop", 5, 1200.50);

		setId(product, 10L);

		GuiActionRunner.execute(() -> view.showProducts(List.of(product)));

		selectProductRow(0);

		clickAsync("assignCategoryButton");

		var dialog = window.dialog(DIALOG_TIMEOUT).requireVisible();

		assertThat(dialog.optionPane().target().getMessage()).isEqualTo("Please select a category");

		dialog.optionPane().okButton().click();
	}

	@Test
	void shouldMakeProductTableNonEditable() {
		assertThat(window.table("productTable").target().getModel().isCellEditable(0, 0)).isFalse();
	}

	private void selectProductRow(int row) {
		GuiActionRunner.execute(() -> window.table("productTable").target().setRowSelectionInterval(row, row));
	}

	private void clickAsync(String buttonName) {
		SwingUtilities.invokeLater(() -> window.button(buttonName).target().doClick());
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