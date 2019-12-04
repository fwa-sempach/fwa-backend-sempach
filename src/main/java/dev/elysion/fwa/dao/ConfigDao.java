package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.ConfigEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class ConfigDao extends AbstractDao<ConfigEntity> {

	@Inject
	public ConfigDao(EntityManager em) {
		super(em);
	}

	protected ConfigDao() {
		// CDI Proxy
	}
}
