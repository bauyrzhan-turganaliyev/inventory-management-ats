package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CategoryTest {

	@Test
	void newCategoryShouldStoreName() {
		Category category = new Category("Electronics");

		assertThat(category.getName()).isEqualTo("Electronics");
	}
}