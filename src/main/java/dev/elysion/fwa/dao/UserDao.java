package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.UserEntity;
import org.apache.commons.collections4.CollectionUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


@RequestScoped
public class UserDao extends AbstractDao<UserEntity> {

	protected UserDao() {
		// used for weld proxy
	}

	@Inject
	public UserDao(EntityManager em) {
		super(em);
	}

	public UserEntity readByUsername(String username) {
		TypedQuery<UserEntity> query = em.createNamedQuery(UserEntity.QUERY_READ_BY_USERNAME, UserEntity.class);
		query.setParameter("username", username);
		List<UserEntity> results = query.getResultList();

		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		else {
			return results.get(0);
		}
	}
}