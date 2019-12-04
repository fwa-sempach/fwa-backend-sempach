package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.TaskEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class TaskDao extends AbstractDao<TaskEntity> {
	protected TaskDao() {
		// used for weld proxy
	}

	@Inject
	public TaskDao(EntityManager em) {
		super(em);
	}
}
