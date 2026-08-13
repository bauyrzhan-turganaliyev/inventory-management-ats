package it.unifi.bautur.store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CategoryTest {

	@Test
	void testCategoryShouldStoreName() {
		Category category = new Category("Electronics");

		assertThat(category.getName()).isEqualTo("Electronics");
	}
	
	@Test
	void testCategoryWithEmptyNameShouldThrow() {
		assertThatThrownBy(() -> new Category("")).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Category name cannot be empty");
	}

	@Test
	void testCategoryWithNullNameShouldThrow() {
		assertThatThrownBy(() -> new Category(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Category name cannot be null");
	}
}