package it.unifi.bautur.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.bautur.store.model.Product;
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

	private InventoryService service;

	@BeforeEach
	void setUp() {
	    service = new InventoryService(transactionManager);
	}
	
	private void setUpTransaction() {
	    when(repositoryProvider.getProductRepository())
	            .thenReturn(productRepository);

	    when(transactionManager.doInTransaction(
	            any()))
	            .thenAnswer(invocation -> {
	                TransactionCode<?> code = invocation.getArgument(0);
	                return code.apply(repositoryProvider);
	            });
	}
	
	@Test
	void getAllProductsShouldReturnAllProducts() {
	    setUpTransaction();

	    Product laptop = new Product("Laptop", 1200.50);
	    Product phone = new Product("Phone", 800.00);

	    List<Product> products = List.of(laptop, phone);

	    when(productRepository.findAll()).thenReturn(products);

	    List<Product> result = service.getAllProducts();

	    assertThat(result).isEqualTo(products);
	}
	
	@Test
	void addProductShouldSaveProduct() {
	    setUpTransaction();

	    Product product = new Product("Laptop", 1200.50);

	    service.addProduct(product);

	    verify(productRepository).save(product);
	}
	
	@Test
	void getProductByIdShouldReturnProduct() {
	    setUpTransaction();

	    Product product = new Product("Laptop", 1200.50);

	    when(productRepository.findById(1L))
	            .thenReturn(Optional.of(product));

	    Optional<Product> result = service.getProductById(1L);

	    assertThat(result).contains(product);
	}
	
	@Test
	void getProductByIdShouldReturnEmptyWhenProductDoesNotExist() {
	    setUpTransaction();

	    when(productRepository.findById(99L))
	            .thenReturn(Optional.empty());

	    Optional<Product> result = service.getProductById(99L);

	    assertThat(result).isEmpty();
	}

	@Test
	void deleteProductShouldDeleteProductById() {
	    setUpTransaction();

	    service.deleteProduct(1L);

	    verify(productRepository).deleteById(1L);
	}
	
	@Test
	void addProductShouldThrowWhenProductIsNull() {
	    assertThatThrownBy(() -> service.addProduct(null))
	            .isInstanceOf(IllegalArgumentException.class)
	            .hasMessage("Product cannot be null");
	}
}