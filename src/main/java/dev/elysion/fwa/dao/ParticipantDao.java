package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.ParticipantEntity;
import dev.elysion.fwa.enumeration.ParticipantStatus;
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
public class ParticipantDao extends AbstractDao<ParticipantEntity> {

	protected ParticipantDao() {
		// used for weld proxy
	}

	@Inject
	public ParticipantDao(EntityManager em) {
		super(em);
	}

	public List<ParticipantEntity> readAll() {
		return super.readAll(ParticipantEntity.QUERY_READ_ALL);
	}

	public List<ParticipantEntity> readPagedAndFiltered(int page, int pageSize, Integer organisationId,
														List<Integer> adIds, List<ParticipantStatus> status,
														String sortField, boolean sortAscending) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<ParticipantEntity> criteriaQuery = criteriaBuilder.createQuery(ParticipantEntity.class);

		Root<ParticipantEntity> participantRoot = criteriaQuery.from(ParticipantEntity.class);

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, participantRoot, organisationId, adIds,
				status);

		criteriaQuery.where(fullPredicate);

		if (sortField != null) {
			if (sortAscending) {
				criteriaQuery.orderBy(criteriaBuilder.asc(createExpression(participantRoot, sortField)));
			}
			else {
				criteriaQuery.orderBy(criteriaBuilder.desc(createExpression(participantRoot, sortField)));
			}
		}

		TypedQuery<ParticipantEntity> query = em.createQuery(criteriaQuery);

		query.setFirstResult(page * pageSize);
		query.setMaxResults(pageSize);

		List<ParticipantEntity> resultList = query.getResultList();
		if (resultList == null) {
			return Collections.emptyList();
		}
		else {
			return resultList;
		}
	}

	public Long readFilteredCount(Integer organisationId, List<Integer> adIds, List<ParticipantStatus> status) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery query = criteriaBuilder.createQuery();

		Root<ParticipantEntity> root = query.from(ParticipantEntity.class);
		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, root, organisationId, adIds, status);

		query.select(criteriaBuilder.count(root));
		query.where(fullPredicate);

		TypedQuery<Long> countQuery = em.createQuery(query);
		return countQuery.getSingleResult();
	}

	private Expression<String> createExpression(Root<ParticipantEntity> root, String fieldName) {
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

	private Predicate createPredicateForFiltering(CriteriaBuilder criteriaBuilder,
												  Root<ParticipantEntity> participantRoot, Integer organisationId,
												  List<Integer> adIds, List<ParticipantStatus> status) {


		Predicate organisationIdPredicate;
		if (organisationId == null) {
			organisationIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			organisationIdPredicate = criteriaBuilder.equal(participantRoot.get("organisationId"), organisationId);
		}

		Predicate categoryIdPredicate;
		if (CollectionUtils.isEmpty(adIds)) {
			categoryIdPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			categoryIdPredicate = participantRoot.get("ad_id")
												 .in(adIds);
		}

		Predicate statusPredicate;
		if (CollectionUtils.isEmpty(status)) {
			statusPredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			statusPredicate = participantRoot.get("status")
											 .in(status);
		}

		Predicate notDeleted = criteriaBuilder.equal(participantRoot.get("deleted"), false);

		return criteriaBuilder.and(organisationIdPredicate, categoryIdPredicate, statusPredicate, notDeleted);
	}


}
