package dev.elysion.fwa.dao;

import dev.elysion.fwa.entity.DocumentEntity;
import dev.elysion.fwa.enumeration.DocumentType;
import org.apache.commons.collections4.CollectionUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class DocumentDao extends AbstractDao<DocumentEntity> {

	protected DocumentDao() {
		// used for weld proxy
	}

	@Inject
	public DocumentDao(EntityManager em) {
		super(em);
	}

	public List<DocumentEntity> readAll() {
		return super.readAll(DocumentEntity.QUERY_READ_ALL);
	}

	public List<DocumentEntity> readFiltered(List<DocumentType> documentTypes) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<DocumentEntity> documentRoot = criteriaQuery.from(DocumentEntity.class);

		Predicate fullPredicate = createPredicateForFiltering(criteriaBuilder, documentRoot, documentTypes);

		criteriaQuery.where(fullPredicate);

		criteriaQuery.multiselect(documentRoot.get("id"), documentRoot.get("filename"), documentRoot.get("documentType"
		));
		List<Tuple> tupleResult = em.createQuery(criteriaQuery)
									.getResultList();
		List<DocumentEntity> documentResultList = new ArrayList<>();
		for (Tuple t : tupleResult) {
			DocumentEntity resultEntity = new DocumentEntity();
			resultEntity.setId((int) t.get(0));
			resultEntity.setFilename((String) t.get(1));
			resultEntity.setDocumentType((DocumentType) t.get(2));
			documentResultList.add(resultEntity);
		}

		return documentResultList;
	}

	private Predicate createPredicateForFiltering(CriteriaBuilder criteriaBuilder, Root<DocumentEntity> documentRoot,
												  List<DocumentType> documentTypes) {

		Predicate documentTypePredicate;
		if (CollectionUtils.isEmpty(documentTypes)) {
			documentTypePredicate = criteriaBuilder.conjunction(); // returns always true
		}
		else {
			documentTypePredicate = documentRoot.get("documentType")
												.in(documentTypes);
		}

		return criteriaBuilder.and(documentTypePredicate);
	}
}

