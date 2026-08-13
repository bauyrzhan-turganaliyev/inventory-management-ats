package it.unifi.bautur.store.model;

public class Category {

	private final String name;

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

	public String getName() {
		return name;
	}

}