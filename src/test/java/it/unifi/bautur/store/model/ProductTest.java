package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductTest {

	@Test
	void testProductShouldStoreNameAndPrice() {
		Product product = new Product("Laptop", 1200.50);

		assertThat(product.getName()).isEqualTo("Laptop");
		assertThat(product.getPrice()).isEqualTo(1200.50);
	}

	@Test
	void testProductShouldStoreAssignedCategory() {
		Product product = new Product("Laptop", 1200.50);
		Category category = new Category("Electronics");

		product.setCategory(category);

		assertThat(product.getCategory()).isEqualTo(category);
	}

	@Test
	void testProductWithEmptyNameShouldThrow() {
		assertThatThrownBy(() -> new Product("", 10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product name cannot be empty");
	}

	@Test
	void testProductWithNullNameShouldThrow() {
		assertThatThrownBy(() -> new Product(null, 10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product name cannot be null");
	}

	@Test
	void testProductConstructorThrowsWhenPriceIsNegative() {
		assertThatThrownBy(() -> new Product("Laptop", -10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product price cannot be negative");
	}

	@Test
	void testProductPriceIsZeroIsAllowed() {
		Product product = new Product("Freebie", 0.0);

		assertThat(product.getPrice()).isEqualTo(0.0);
	}

	@Test
	void testProductShouldHaveNoCategory() {
		Product product = new Product("Laptop", 1200.50);

		assertThat(product.getCategory()).isNull();
	}
	@Test
	void testProductShouldHaveNoId() {
	    Product product = new Product("Laptop", 1200.50);

	    assertThat(product.getId()).isNull();
	}
}