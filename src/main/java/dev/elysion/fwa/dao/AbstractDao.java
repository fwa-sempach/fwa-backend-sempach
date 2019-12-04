package dev.elysion.fwa.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDao<T> {

	protected EntityManager em;

	private Class<T> type;

	public AbstractDao() {

	}

	public AbstractDao(EntityManager em) {
		this.em = em;

		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class<T>) pt.getActualTypeArguments()[0];
	}

	public void persist(T t) {
		if (!em.getTransaction()
			   .isActive()) {
			em.getTransaction()
			  .begin();
			this.em.persist(t);
			em.getTransaction()
			  .commit();
		}
		else {
			this.em.persist(t);
		}
	}

	public void delete(T t) {
		if (!em.getTransaction()
			   .isActive()) {
			em.getTransaction()
			  .begin();
			this.em.remove(t);
			em.getTransaction()
			  .commit();
		}
		else {
			this.em.remove(t);
		}
	}

	public void deleteAll(List<T> ts) {
		if (!em.getTransaction()
			   .isActive()) {
			em.getTransaction()
			  .begin();
			ts.stream()
			  .forEach(this::delete);
			em.getTransaction()
			  .commit();
		}
		else {
			ts.stream()
			  .forEach(this::delete);
		}
	}

	public T merge(T t) {
		if (!em.getTransaction()
			   .isActive()) {
			em.getTransaction()
			  .begin();
			T result = this.em.merge(t);
			em.getTransaction()
			  .commit();
			return result;
		}
		else {
			return em.merge(t);
		}
	}

	public List<T> mergeAll(List<T> ts) {
		List<T> results = new LinkedList<>();
		if (!em.getTransaction()
			   .isActive()) {
			em.getTransaction()
			  .begin();
			ts.stream()
			  .forEach(t -> results.add(merge(t)));
			em.getTransaction()
			  .commit();
		}
		else {
			ts.stream()
			  .forEach(t -> results.add(merge(t)));
		}

		return results;
	}

	public void detach(T t) {
		em.detach(t);
	}

	public void refresh(T t) {
		em.refresh(t);
	}

	public T readById(int id) {
		T result = em.find(type, id);
		if (result != null) {
			em.refresh(result);
		}

		return result;
	}

	public List<T> readAll(String namedQueryName) {
		TypedQuery<T> query = em.createNamedQuery(namedQueryName, type);
		List<T> resultList = query.getResultList();

		if (resultList == null) {
			resultList = Collections.emptyList();
		}

		return resultList;
	}

}

