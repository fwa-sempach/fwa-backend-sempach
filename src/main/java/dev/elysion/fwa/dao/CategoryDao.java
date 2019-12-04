package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.CategoryEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@RequestScoped
public class CategoryDao extends AbstractDao<CategoryEntity> {

	protected CategoryDao() {
		// used for weld proxy
	}

	@Inject
	public CategoryDao(EntityManager em) {
		super(em);
	}

	public List<CategoryEntity> readAll() {
		return super.readAll(CategoryEntity.QUERY_READ_ALL);
	}


}
