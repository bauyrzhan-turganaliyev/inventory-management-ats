package it.unifi.bautur.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.repository.CategoryRepository;
import it.unifi.bautur.store.repository.ProductRepository;
import it.unifi.bautur.store.repository.RepositoryProvider;
import it.unifi.bautur.store.repository.TransactionCode;
import it.unifi.bautur.store.repository.TransactionManager;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private RepositoryProvider repositoryProvider;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	private InventoryService service;

	@BeforeEach
	void setUp() {
		service = new InventoryService(transactionManager);
	}

	private void setUpTransaction() {
		when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
			TransactionCode<?> code = invocation.getArgument(0);
			return code.apply(repositoryProvider);
		});
	}

	private void setUpProductTransaction() {
		setUpTransaction();

		when(repositoryProvider.getProductRepository()).thenReturn(productRepository);
	}

	private void setUpCategoryTransaction() {
		setUpTransaction();

		when(repositoryProvider.getCategoryRepository()).thenReturn(categoryRepository);
	}

	@Test
	void getAllProductsShouldReturnAllProducts() {
		setUpProductTransaction();

		Product laptop = new Product("Laptop", 5, 1200.50);
		Product phone = new Product("Phone", 10, 800.00);

		List<Product> products = List.of(laptop, phone);

		when(productRepository.findAll()).thenReturn(products);

		List<Product> result = service.getAllProducts();

		assertThat(result).isEqualTo(products);
	}

	@Test
	void addProductShouldSaveProduct() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);

		service.addProduct(product);

		verify(productRepository).save(product);
	}

	@Test
	void getProductByIdShouldReturnProduct() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		Optional<Product> result = service.getProductById(1L);

		assertThat(result).contains(product);
	}

	@Test
	void getProductByIdShouldReturnEmptyWhenProductDoesNotExist() {
		setUpProductTransaction();

		when(productRepository.findById(99L)).thenReturn(Optional.empty());

		Optional<Product> result = service.getProductById(99L);

		assertThat(result).isEmpty();
	}

	@Test
	void deleteProductShouldDeleteProductById() {
		setUpProductTransaction();

		service.deleteProduct(1L);

		verify(productRepository).deleteById(1L);
	}

	@Test
	void addProductShouldThrowWhenProductIsNull() {
		assertThatThrownBy(() -> service.addProduct(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product cannot be null");
	}

	@Test
	void deleteProductShouldThrowWhenIdIsNull() {
		assertThatThrownBy(() -> service.deleteProduct(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product id cannot be null");
	}

	@Test
	void updateProductStockShouldUpdateQuantityAndSaveProduct() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		service.updateProductStock(1L, 10);

		assertThat(product.getQuantity()).isEqualTo(10);

		verify(productRepository).save(product);
		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	void updateProductStockShouldAllowZeroQuantity() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		service.updateProductStock(1L, 0);

		assertThat(product.getQuantity()).isZero();

		verify(productRepository).save(product);
	}

	@Test
	void updateProductStockShouldThrowWhenProductDoesNotExist() {
		setUpProductTransaction();

		when(productRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.updateProductStock(99L, 10)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product not found");

		verify(productRepository, never()).save(any());
	}

	@Test
	void updateProductStockShouldThrowWhenIdIsNull() {
		assertThatThrownBy(() -> service.updateProductStock(null, 10)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product id cannot be null");

		verify(transactionManager, never()).doInTransaction(any());
	}

	@Test
	void updateProductStockShouldThrowWhenQuantityIsNegative() {
		assertThatThrownBy(() -> service.updateProductStock(1L, -1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product quantity cannot be negative");

		verify(transactionManager, never()).doInTransaction(any());
	}

	@Test
	void assignCategoryToProductShouldAssignCategoryAndSaveProduct() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);
		Category category = new Category("Electronics");

		when(repositoryProvider.getCategoryRepository()).thenReturn(categoryRepository);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

		service.assignCategoryToProduct(1L, 2L);

		assertThat(product.getCategory()).isEqualTo(category);

		verify(productRepository).save(product);
		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	void assignCategoryToProductShouldThrowWhenProductDoesNotExist() {
		setUpProductTransaction();

		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.assignCategoryToProduct(1L, 2L)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product not found");

		verify(categoryRepository, never()).findById(any());
	}

	@Test
	void assignCategoryToProductShouldThrowWhenCategoryDoesNotExist() {
		setUpProductTransaction();

		Product product = new Product("Laptop", 5, 1200.50);

		when(repositoryProvider.getCategoryRepository()).thenReturn(categoryRepository);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.assignCategoryToProduct(1L, 99L)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Category not found");

		verify(productRepository, never()).save(any());
	}

	@Test
	void getAllCategoriesShouldReturnAllCategories() {
		setUpCategoryTransaction();

		Category electronics = new Category("Electronics");
		Category books = new Category("Books");

		List<Category> categories = List.of(electronics, books);

		when(categoryRepository.findAll()).thenReturn(categories);

		List<Category> result = service.getAllCategories();

		assertThat(result).isEqualTo(categories);
	}
}