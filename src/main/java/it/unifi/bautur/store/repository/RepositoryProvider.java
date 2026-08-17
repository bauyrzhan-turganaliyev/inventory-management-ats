package it.unifi.bautur.store.repository;

public interface RepositoryProvider {
	ProductRepository getProductRepository();

	CategoryRepository getCategoryRepository();
}
