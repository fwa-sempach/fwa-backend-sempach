package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.BasicConditionEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class BasicConditionDao extends AbstractDao<BasicConditionEntity> {
	protected BasicConditionDao() {
		// used for weld proxy
	}

	@Inject
	public BasicConditionDao(EntityManager em) {
		super(em);
	}
}
