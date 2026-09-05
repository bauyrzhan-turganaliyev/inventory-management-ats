package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductTest {

	@Test
	void testProductShouldStoreNameQuantityAndPrice() {
		Product product = new Product("Laptop", 5, 1200.50);

		assertThat(product.getName()).isEqualTo("Laptop");
		assertThat(product.getQuantity()).isEqualTo(5);
		assertThat(product.getPrice()).isEqualTo(1200.50);
	}

	@Test
	void testProductShouldDefaultQuantityToZero() {
		Product product = new Product("Laptop", 1200.50);

		assertThat(product.getQuantity()).isZero();
	}

	@Test
	void testProductShouldStoreAssignedCategory() {
		Product product = new Product("Laptop", 5, 1200.50);
		Category category = new Category("Electronics");

		product.setCategory(category);

		assertThat(product.getCategory()).isEqualTo(category);
	}

	@Test
	void testProductWithEmptyNameShouldThrow() {
		assertThatThrownBy(() -> new Product("", 5, 10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product name cannot be empty");
	}

	@Test
	void testProductWithNullNameShouldThrow() {
		assertThatThrownBy(() -> new Product(null, 5, 10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product name cannot be null");
	}

	@Test
	void testProductConstructorThrowsWhenQuantityIsNegative() {
		assertThatThrownBy(() -> new Product("Laptop", -1, 10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product quantity cannot be negative");
	}

	@Test
	void testProductQuantityZeroIsAllowed() {
		Product product = new Product("Laptop", 0, 10.0);

		assertThat(product.getQuantity()).isZero();
	}

	@Test
	void testProductShouldUpdateQuantity() {
		Product product = new Product("Laptop", 5, 1200.50);

		product.setQuantity(10);

		assertThat(product.getQuantity()).isEqualTo(10);
	}

	@Test
	void testProductShouldAllowQuantityToBeUpdatedToZero() {
		Product product = new Product("Laptop", 5, 1200.50);

		product.setQuantity(0);

		assertThat(product.getQuantity()).isZero();
	}

	@Test
	void testProductShouldRejectNegativeQuantityWhenUpdating() {
		Product product = new Product("Laptop", 5, 1200.50);

		assertThatThrownBy(() -> product.setQuantity(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product quantity cannot be negative");
	}

	@Test
	void testProductConstructorThrowsWhenPriceIsNegative() {
		assertThatThrownBy(() -> new Product("Laptop", 5, -10.0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product price cannot be negative");
	}

	@Test
	void testProductPriceIsZeroIsAllowed() {
		Product product = new Product("Freebie", 5, 0.0);

		assertThat(product.getPrice()).isEqualTo(0.0);
	}

	@Test
	void testProductShouldHaveNoCategory() {
		Product product = new Product("Laptop", 5, 1200.50);

		assertThat(product.getCategory()).isNull();
	}

	@Test
	void testProductShouldHaveNoId() {
		Product product = new Product("Laptop", 5, 1200.50);

		assertThat(product.getId()).isNull();
	}
}