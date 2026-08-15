package it.unifi.bautur.store.service;

import java.util.List;

import it.unifi.bautur.store.model.Product;
import it.unifi.bautur.store.repository.TransactionManager;

public class InventoryService {

    private final TransactionManager transactionManager;

    public InventoryService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public List<Product> getAllProducts() {
        return transactionManager.doInTransaction(
                provider -> provider.getProductRepository().findAll()
        );
    }
}