package it.unifi.bautur.store.repository;

public interface TransactionManager {

	<T> T doInTransaction(TransactionCode<T> work);
}