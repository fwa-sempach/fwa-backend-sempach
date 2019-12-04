package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.PersonEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class PersonDao extends AbstractDao<PersonEntity> {

	protected PersonDao() {
		// used for weld proxy
	}

	@Inject
	public PersonDao(EntityManager em) {
		super(em);
	}
}
