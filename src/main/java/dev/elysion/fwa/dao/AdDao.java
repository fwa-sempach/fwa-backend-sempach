package dev.elysion.fwa.dao;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.entity.AdEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequestScoped
public class AdDao extends AbstractDao<AdEntity> {
	protected AdDao() {
		// used for weld proxy
	}

	@Inject
	public AdDao(EntityManager em) {
		super(em);
	}

	public List<AdEntity> readAll() {
		return super.readAll(AdEntity.QUERY_READ_ALL);
	}

	// FIXME: optimize abstractly according to: https://stackoverflow
	// .com/questions/2883887/in-jpa-2-using-a-criteriaquery-how-to-count-results
	public List<AdEntity> readPagedAndFiltered(int page, int pageSize, List<Integer> categoryIds,
											   List<Integer> organisationIds, String sortField, boolean sortAscending,
											   boolean onlyActive) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<AdEntity> criteriaQuery = criteriaBuilder.createQuery(AdEntity.class);

		Root<AdEntity> adRoot = criteriaQuery.from(AdEntity.class);

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, adRoot, categoryIds, organisationIds,
				onlyActive);

		criteriaQuery.where(fullPredicate);

		if (sortField != null) {
			if (sortAscending) {
				criteriaQuery.orderBy(criteriaBuilder.asc(createExpression(adRoot, sortField)));
			}
			else {
				criteriaQuery.orderBy(criteriaBuilder.desc(createExpression(adRoot, sortField)));
			}
		}

		TypedQuery<AdEntity> query = em.createQuery(criteriaQuery);

		query.setFirstResult(page * pageSize);
		query.setMaxResults(pageSize);

		List<AdEntity> resultList = query.getResultList();
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

		Root<AdEntity> root = query.from(AdEntity.class);
		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, root, categoryIds, organisationIds,
				onlyActive);

		query.select(criteriaBuilder.count(root));
		query.where(fullPredicate);

		TypedQuery<Long> countQuery = em.createQuery(query);
		return countQuery.getSingleResult();
	}

	private Expression<String> createExpression(Root<AdEntity> root, String fieldName) {
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

	private Predicate createPredicateForFiltering(CriteriaBuilder criteriaBuilder, Root<AdEntity> adRoot,
												  List<Integer> categoryIds, List<Integer> organisationIds,
												  boolean onlyActive) {

		Predicate categoryIdPredicate;
		if (CollectionUtils.isEmpty(categoryIds)) {
			categoryIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			categoryIdPredicate = adRoot.get("offer")
										.get("category")
										.get("id")
										.in(categoryIds);
		}

		Predicate organisationIdPredicate;
		if (CollectionUtils.isEmpty(organisationIds)) {
			organisationIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			organisationIdPredicate = adRoot.get("offer")
											.get("organisation")
											.get("id")
											.in(organisationIds);
		}

		Predicate isActive;
		Predicate isOrgVerified;
		Predicate released;
		Predicate notExpired;

		Date inputDate = Converter.convertToDate(LocalDateTime.now());

		if (onlyActive) {
			isActive = criteriaBuilder.equal(adRoot.get("active"), true);
			isOrgVerified = criteriaBuilder.equal(adRoot.get("offer")
														.get("organisation")
														.get("verified"), true);
			released = criteriaBuilder.greaterThanOrEqualTo(adRoot.get("validUntil"), inputDate);
			notExpired = criteriaBuilder.lessThanOrEqualTo(adRoot.get("releaseDate"), inputDate);
		}
		else {
			isActive = criteriaBuilder.conjunction();
			isOrgVerified = criteriaBuilder.conjunction();
			released = criteriaBuilder.conjunction();
			notExpired = criteriaBuilder.conjunction();
		}

		Predicate notDeleted = criteriaBuilder.equal(adRoot.get("deleted"), false);

		return criteriaBuilder.and(categoryIdPredicate, organisationIdPredicate, isActive, isOrgVerified, notDeleted,
				released, notExpired);
	}
}
