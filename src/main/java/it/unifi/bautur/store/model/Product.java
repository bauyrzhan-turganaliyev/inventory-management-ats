package it.unifi.bautur.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private double price;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Generated
	protected Product() {
		// Required by JPA
	}

	public Product(String name, double price) {
		validate(name, price);
		this.name = name;
		this.price = price;
	}

	private void validate(String name, double price) {
		if (name == null) {
			throw new IllegalArgumentException("Product name cannot be null");
		}

		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("Product name cannot be empty");
		}

		if (price < 0) {
			throw new IllegalArgumentException("Product price cannot be negative");
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}