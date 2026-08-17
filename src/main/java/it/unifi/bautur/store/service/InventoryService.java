package it.unifi.bautur.store.service;

import java.util.List;
import java.util.Optional;

import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.repository.TransactionManager;

public class InventoryService {

	private final TransactionManager transactionManager;

	public InventoryService(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<Product> getAllProducts() {
		return transactionManager.doInTransaction(provider -> provider.getProductRepository().findAll());
	}

	public void addProduct(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("Product cannot be null");
		}

		transactionManager.doInTransaction(provider -> {
			provider.getProductRepository().save(product);
			return null;
		});
	}

	public Optional<Product> getProductById(Long id) {
		return transactionManager.doInTransaction(provider -> provider.getProductRepository().findById(id));
	}

	public void deleteProduct(Long id) {
	    if (id == null) {
	        throw new IllegalArgumentException("Product id cannot be null");
	    }

	    transactionManager.doInTransaction(provider -> {
	        provider.getProductRepository().deleteById(id);
	        return null;
	    });
	}
}