package it.unifi.bautur.store.view;

import java.util.List;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.service.InventoryService;

public class InventoryPresenter {

	private final InventoryView view;
	private final InventoryService service;

	public InventoryPresenter(InventoryView view, InventoryService service) {
		this.view = view;
		this.service = service;
	}

	public void loadProducts() {
		try {
			List<Product> products = service.getAllProducts();

			view.showProducts(products);
		} catch (RuntimeException exception) {
			showError(exception);
		}
	}

	public void loadCategories() {
		try {
			List<Category> categories = service.getAllCategories();

			view.showCategories(categories);
		} catch (RuntimeException exception) {
			showError(exception);
		}
	}

	public void addProduct(String name, double price) {
		try {
			Product product = new Product(name, price);

			service.addProduct(product);

			loadProducts();
		} catch (RuntimeException exception) {
			showError(exception);
		}
	}

	public void deleteProduct(Long productId) {
		try {
			service.deleteProduct(productId);

			loadProducts();
		} catch (RuntimeException exception) {
			showError(exception);
		}
	}

	public void assignCategory(Long productId, Long categoryId) {
		try {
			service.assignCategoryToProduct(productId, categoryId);

			loadProducts();
		} catch (RuntimeException exception) {
			showError(exception);
		}
	}

	private void showError(RuntimeException exception) {
		view.showError(exception.getMessage());
	}
}