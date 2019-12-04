package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.OfferEntity;
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
public class OfferDao extends AbstractDao<OfferEntity> {

	protected OfferDao() {
		// used for weld proxy
	}

	@Inject
	public OfferDao(EntityManager em) {
		super(em);
	}

	public List<OfferEntity> readAll() {
		return super.readAll(OfferEntity.QUERY_READ_ALL);
	}

	public List<OfferEntity> readByOrganisationId(int organisationId) {
		TypedQuery<OfferEntity> query = em.createNamedQuery(OfferEntity.QUERY_READ_BY_ORGANISATION_ID,
				OfferEntity.class);
		query.setParameter("organisationId", organisationId);
		return query.getResultList();
	}

	// FIXME: optimize abstractly according to: https://stackoverflow
	// .com/questions/2883887/in-jpa-2-using-a-criteriaquery-how-to-count-results
	public List<OfferEntity> readPagedAndFiltered(int page, int pageSize, List<Integer> categoryIds,
												  List<Integer> organisationIds, boolean onlyActive, String sortField,
												  boolean sortAscending) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<OfferEntity> criteriaQuery = criteriaBuilder.createQuery(OfferEntity.class);

		Root<OfferEntity> offerRoot = criteriaQuery.from(OfferEntity.class);

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, offerRoot, categoryIds, organisationIds
				, onlyActive);

		criteriaQuery.where(fullPredicate);

		if (sortField != null) {
			if (sortAscending) {
				criteriaQuery.orderBy(criteriaBuilder.asc(createExpression(offerRoot, sortField)));
			}
			else {
				criteriaQuery.orderBy(criteriaBuilder.desc(createExpression(offerRoot, sortField)));
			}
		}

		TypedQuery<OfferEntity> query = em.createQuery(criteriaQuery);

		query.setFirstResult(page * pageSize);
		query.setMaxResults(pageSize);

		List<OfferEntity> resultList = query.getResultList();
		if (resultList == null) {
			return Collections.emptyList();
		}
		else {
			return resultList;
		}
	}

	public Long readFilteredCount(List<Integer> categoryIds, List<Integer> organisationIds, boolean onlyActive) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery query = criteriaBuilder.createQuery();

		Root<OfferEntity> root = query.from(OfferEntity.class);
		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, root, categoryIds, organisationIds,
				onlyActive);

		query.select(criteriaBuilder.count(root));
		query.where(fullPredicate);

		TypedQuery<Long> countQuery = em.createQuery(query);
		return countQuery.getSingleResult();
	}

	private Expression<String> createExpression(Root<OfferEntity> root, String fieldName) {
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

	private Predicate createPredicateForFiltering(CriteriaBuilder criteriaBuilder, Root<OfferEntity> offerRoot,
												  List<Integer> categoryIds, List<Integer> organisationIds,
												  boolean onlyActive) {

		Predicate categoryIdPredicate;
		if (CollectionUtils.isEmpty(categoryIds)) {
			categoryIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			categoryIdPredicate = offerRoot.get("category")
										   .get("id")
										   .in(categoryIds);
		}

		Predicate organisationIdPredicate;
		if (CollectionUtils.isEmpty(organisationIds)) {
			organisationIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			organisationIdPredicate = offerRoot.get("organisation")
											   .get("id")
											   .in(organisationIds);
		}

		Predicate isActive;
		Predicate isOrgVerified;
		if (onlyActive) {
			isActive = criteriaBuilder.equal(offerRoot.get("active"), true);
			isOrgVerified = criteriaBuilder.equal(offerRoot.get("organisation")
														   .get("verified"), true);
		}
		else {
			isActive = criteriaBuilder.conjunction();
			isOrgVerified = criteriaBuilder.conjunction();
		}

		Predicate notDeleted = criteriaBuilder.equal(offerRoot.get("deleted"), false);

		return criteriaBuilder.and(categoryIdPredicate, organisationIdPredicate, isActive, isOrgVerified, notDeleted);
	}
}