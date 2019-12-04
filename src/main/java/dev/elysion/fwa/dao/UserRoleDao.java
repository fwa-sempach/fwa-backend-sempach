package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.UserRoleEntity;
import dev.elysion.fwa.enumeration.Role;
import org.apache.commons.collections4.CollectionUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


@RequestScoped
public class UserRoleDao extends AbstractDao<UserRoleEntity> {

	protected UserRoleDao() {
		// used for weld proxy
	}

	@Inject
	public UserRoleDao(EntityManager em) {
		super(em);
	}

	public UserRoleEntity readByUserIdRole(int userId, Role role) {
		TypedQuery<UserRoleEntity> query = em.createNamedQuery(UserRoleEntity.QUERY_READ_BY_USERID_ROLE,
				UserRoleEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("role", role);
		List<UserRoleEntity> results = query.getResultList();

		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		else {
			return results.get(0);
		}
	}
}