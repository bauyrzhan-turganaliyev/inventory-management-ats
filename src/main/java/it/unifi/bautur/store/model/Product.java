package it.unifi.bautur.store.model;

public class Product {

	private final String name;
	private final double price;

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

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
}