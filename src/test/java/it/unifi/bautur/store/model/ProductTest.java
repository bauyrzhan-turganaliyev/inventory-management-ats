package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductTest {

	@Test
	void testProductShouldStoreNameAndPrice() {
		Product product = new Product("Laptop", 1200.50);

		assertThat(product.getName()).isEqualTo("Laptop");
		assertThat(product.getPrice()).isEqualTo(1200.50);
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
    	IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Product("Laptop", -10.0));
    
    	assertEquals("Product price cannot be negative", exception.getMessage());
    }
    
    @Test
    void testProductPriceIsZeroIsAllowed() {
        Product product = new Product("Freebie", 0.0);
        assertEquals(0.0, product.getPrice());
    }
}