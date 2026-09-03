package it.unifi.bautur.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Generated
	protected Category() {
		// Required by JPA
	}

	public Category(String name) {
		validate(name);
		this.name = name;
	}

	private void validate(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Category name cannot be null");
		}

		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("Category name cannot be empty");
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}