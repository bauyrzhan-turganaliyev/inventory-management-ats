package it.unifi.bautur.store.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.service.InventoryService;

@ExtendWith(MockitoExtension.class)
class InventoryPresenterTest {

	@Mock
	private InventoryView view;

	@Mock
	private InventoryService service;

	private InventoryPresenter presenter;

	@BeforeEach
	void setUp() {
		presenter = new InventoryPresenter(view, service);
	}

	@Test
	void shouldLoadProducts() {
		List<Product> products = List.of(new Product("Laptop", 1200.50), new Product("Phone", 800.00));

		when(service.getAllProducts()).thenReturn(products);

		presenter.loadProducts();

		verify(service).getAllProducts();
		verify(view).showProducts(products);
	}

	@Test
	void shouldShowErrorWhenLoadingProductsFails() {
		when(service.getAllProducts()).thenThrow(new IllegalStateException("Cannot load products"));

		presenter.loadProducts();

		verify(view).showError("Cannot load products");

		verify(view, never()).showProducts(anyList());
	}

	@Test
	void shouldLoadCategories() {
		List<Category> categories = List.of(new Category("Electronics"), new Category("Books"));

		when(service.getAllCategories()).thenReturn(categories);

		presenter.loadCategories();

		verify(service).getAllCategories();
		verify(view).showCategories(categories);
	}

	@Test
	void shouldShowErrorWhenLoadingCategoriesFails() {
		when(service.getAllCategories()).thenThrow(new IllegalStateException("Cannot load categories"));

		presenter.loadCategories();

		verify(view).showError("Cannot load categories");

		verify(view, never()).showCategories(anyList());
	}

	@Test
	void shouldAddProductAndReloadProducts() {
		List<Product> products = List.of(new Product("Laptop", 1200.50));

		when(service.getAllProducts()).thenReturn(products);

		presenter.addProduct("Laptop", 1200.50);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

		verify(service).addProduct(captor.capture());

		Product product = captor.getValue();

		assertThat(product.getName()).isEqualTo("Laptop");

		assertThat(product.getPrice()).isEqualTo(1200.50);

		verify(service).getAllProducts();
		verify(view).showProducts(products);
	}

	@Test
	void shouldShowErrorWhenAddingProductFails() {
		Product invalidProduct = new Product("Laptop", 1200.50);

		doThrow(new IllegalStateException("Cannot add product")).when(service).addProduct(any(Product.class));

		presenter.addProduct(invalidProduct.getName(), invalidProduct.getPrice());

		verify(view).showError("Cannot add product");

		verify(service, never()).getAllProducts();
	}

	@Test
	void shouldShowErrorWhenProductDataIsInvalid() {
		presenter.addProduct("", 1200.50);

		verify(view).showError("Product name cannot be empty");

		verify(service, never()).addProduct(any(Product.class));
	}

	@Test
	void shouldDeleteProductAndReloadProducts() {
		List<Product> products = List.of();

		when(service.getAllProducts()).thenReturn(products);

		presenter.deleteProduct(10L);

		verify(service).deleteProduct(10L);

		verify(service).getAllProducts();

		verify(view).showProducts(products);
	}

	@Test
	void shouldShowErrorWhenDeletingProductFails() {
		doThrow(new IllegalStateException("Cannot delete product")).when(service).deleteProduct(10L);

		presenter.deleteProduct(10L);

		verify(view).showError("Cannot delete product");

		verify(service, never()).getAllProducts();
	}

	@Test
	void shouldAssignCategoryAndReloadProducts() {
		List<Product> products = List.of(new Product("Laptop", 1200.50));

		when(service.getAllProducts()).thenReturn(products);

		presenter.assignCategory(10L, 20L);

		verify(service).assignCategoryToProduct(10L, 20L);

		verify(service).getAllProducts();

		verify(view).showProducts(products);
	}

	@Test
	void shouldShowErrorWhenAssigningCategoryFails() {
		doThrow(new IllegalArgumentException("Category not found")).when(service).assignCategoryToProduct(10L, 20L);

		presenter.assignCategory(10L, 20L);

		verify(view).showError("Category not found");

		verify(service, never()).getAllProducts();
	}
}