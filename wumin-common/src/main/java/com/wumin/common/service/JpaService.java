package com.wumin.common.service;

import com.wumin.common.persistence.search.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface JpaService<T, ID extends Serializable> {

	public T get(ID id);

	public List<T> getAll();

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T save(T entity);

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T saveAndFlush(T entity);

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(ID id);

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(final T entity);

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteInBatch(final ID[] ids);

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteInBatch(final List<T> entities);
	
	public List<T> getList(SearchRequest searchRequest);
	
	public Page<T> getPage(SearchRequest searchRequest);
	
	public Long count(SearchRequest searchRequest);
	
}
