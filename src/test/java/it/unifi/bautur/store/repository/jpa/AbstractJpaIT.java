package it.unifi.bautur.store.repository.jpa;

import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

abstract class AbstractJpaIT {

	protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

	protected static final EntityManagerFactory ENTITY_MANAGER_FACTORY;

	static {
		POSTGRES.start();

		Map<String, Object> properties = Map.of("jakarta.persistence.jdbc.url", POSTGRES.getJdbcUrl(),
				"jakarta.persistence.jdbc.user", POSTGRES.getUsername(), "jakarta.persistence.jdbc.password",
				POSTGRES.getPassword(), "jakarta.persistence.jdbc.driver", "org.postgresql.Driver",
				"hibernate.hbm2ddl.auto", "create-drop");

		ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("inventory-pu", properties);
	}
}