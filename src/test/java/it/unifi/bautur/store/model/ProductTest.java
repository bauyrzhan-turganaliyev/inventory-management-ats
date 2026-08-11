package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void newProductShouldStoreNameAndPrice() {
        Product product = new Product("Laptop", 1200.50);

        assertThat(product.getName()).isEqualTo("Laptop");
        assertThat(product.getPrice()).isEqualTo(1200.50);
    }
}