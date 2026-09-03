package it.unifi.bautur.store.repository.jpa;

import it.unifi.bautur.store.repository.TransactionCode;
import it.unifi.bautur.store.repository.TransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class JpaTransactionManager implements TransactionManager {

	private final EntityManagerFactory entityManagerFactory;

	public JpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public <T> T doInTransaction(TransactionCode<T> work) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			JpaRepositoryProvider provider = new JpaRepositoryProvider(entityManager);

			T result = work.apply(provider);

			transaction.commit();

			return result;

		} catch (RuntimeException exception) {
			transaction.rollback();
			throw exception;

		} finally {
			entityManager.close();
		}
	}
}