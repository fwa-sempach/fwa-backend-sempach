package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.OrganisationEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;


@RequestScoped
public class OrganisationDao extends AbstractDao<OrganisationEntity> {

	protected OrganisationDao() {
		// used for weld proxy
	}

	@Inject
	public OrganisationDao(EntityManager em) {
		super(em);
	}

	public OrganisationEntity readByUserId(int userId) {
		TypedQuery<OrganisationEntity> query = em.createNamedQuery(OrganisationEntity.QUERY_READ_BY_USER_ID,
				OrganisationEntity.class);
		query.setParameter("userId", userId);

		List<OrganisationEntity> results = query.getResultList();

		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		else {
			return results.get(0);
		}
	}

	public List<OrganisationEntity> readAll() {
		return super.readAll(OrganisationEntity.QUERY_READ_ALL);
	}

	public List<OrganisationEntity> readPagedAndFiltered(int page, int pageSize, String sortField,
														 boolean sortAscending, boolean onlyVerified) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<OrganisationEntity> criteriaQuery = criteriaBuilder.createQuery(OrganisationEntity.class);

		Root<OrganisationEntity> root = criteriaQuery.from(OrganisationEntity.class);

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, root, onlyVerified);
		criteriaQuery.where(fullPredicate);

		if (sortField != null) {
			if (sortAscending) {
				criteriaQuery.orderBy(criteriaBuilder.asc(createExpression(root, sortField)));
			}
			else {
				criteriaQuery.orderBy(criteriaBuilder.desc(createExpression(root, sortField)));
			}
		}

		TypedQuery<OrganisationEntity> query = em.createQuery(criteriaQuery);

		query.setFirstResult(page * pageSize);
		query.setMaxResults(pageSize);

		List<OrganisationEntity> resultList = query.getResultList();
		if (resultList == null) {
			return Collections.emptyList();
		}
		else {
			return resultList;
		}
	}

	public Long readFilteredCount(boolean onlyVerified) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery query = criteriaBuilder.createQuery();

		Root<OrganisationEntity> root = query.from(OrganisationEntity.class);
		query.select(criteriaBuilder.count(root));

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, root, onlyVerified);
		query.where(fullPredicate);

		TypedQuery<Long> countQuery = em.createQuery(query);
		return countQuery.getSingleResult();
	}

	private Predicate createPredicateForFiltering(CriteriaBuilder criteriaBuilder, Root<OrganisationEntity> root,
												  boolean onlyVerified) {
		Predicate isVerified;
		if (onlyVerified) {
			isVerified = criteriaBuilder.equal(root.get("verified"), true);
		}
		else {
			isVerified = criteriaBuilder.conjunction();
		}

		Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);

		return criteriaBuilder.and(isVerified, notDeleted);
	}

	private Expression<String> createExpression(Root<OrganisationEntity> root, String fieldName) {
		String[] splitted = StringUtils.split(fieldName, '.');
		Path<String> result = null;

		// TODO: this could probably optimized
		for (String item : splitted) {
			if (result == null) {
				result = root.get(item);
			}
			else {
				result = result.get(item);
			}
		}

		return result;
	}
}