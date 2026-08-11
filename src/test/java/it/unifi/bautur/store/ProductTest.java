package it.unifi.bautur.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void testProductAttributes() {
        Product product = new Product("Laptop", 1200.50);
        
        assertEquals("Laptop", product.getName());
        assertEquals(1200.50, product.getPrice());
        
        assertEquals(null, product.getId()); 
        assertEquals(null, product.getCategory());
    }

    @Test
    void testProductPriceIsZeroIsAllowed() {
        Product product = new Product("Freebie", 0.0);
        assertEquals(0.0, product.getPrice());
    }
    
}