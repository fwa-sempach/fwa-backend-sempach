package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.CategoryDao;
import dev.elysion.fwa.dto.Category;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class CategoryService {
	private CategoryDao categoryDao;

	protected CategoryService() {
		//Used for proxy
	}

	@Inject
	public CategoryService(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	public List<Category> readAllDtos() {
		return categoryDao.readAll()
						  .stream()
						  .map(Converter::convert)
						  .collect(Collectors.toList());
	}
}
