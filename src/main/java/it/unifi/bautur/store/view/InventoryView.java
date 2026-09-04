package it.unifi.bautur.store.view;

import java.util.List;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;

public interface InventoryView {

	void showProducts(List<Product> products);

	void showCategories(List<Category> categories);

	void showError(String message);
}