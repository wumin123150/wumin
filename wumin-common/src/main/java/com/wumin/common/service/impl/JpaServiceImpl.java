package com.wumin.common.service.impl;

import com.wumin.common.collection.ArrayUtil;
import com.wumin.common.collection.CollectionUtil;
import com.wumin.common.dao.JpaDao;
import com.wumin.common.entity.CreateRecord;
import com.wumin.common.entity.DeleteRecord;
import com.wumin.common.entity.UpdateRecord;
import com.wumin.common.persistence.DynamicSpecification;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.common.reflect.ClassUtil;
import com.wumin.common.service.JpaService;
import com.wumin.common.web.ThreadContext;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class JpaServiceImpl<T, ID extends Serializable> implements JpaService<T, ID> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JpaServiceImpl.class);

  protected JpaDao<T, ID> dao;
  protected Class<T> entityClazz;
  protected JPAQueryFactory queryFactory;

  private EntityManager entityManager;

  public JpaServiceImpl() {
    this.entityClazz = ClassUtil.getClassGenricType(getClass(), 0);
  }

  @PostConstruct
  public void init() {
    queryFactory = new JPAQueryFactory(entityManager);
  }

  public T get(ID id) {
    return dao.getOne(id);
  }

  public List<T> getAll() {
    return dao.findAll();
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public T save(T entity) {
    if(entity instanceof CreateRecord && !((CreateRecord) entity).isCreated()) {
      ((CreateRecord) entity).setCreateTime(new Date());
      ((CreateRecord) entity).setCreator(ThreadContext.getUserId());
    }
    if(entity instanceof DeleteRecord && !((DeleteRecord) entity).isCreated()) {
      ((DeleteRecord) entity).setDelFlag(false);
    }
    if (entity instanceof UpdateRecord) {
      ((UpdateRecord) entity).setUpdateTime(new Date());
      ((UpdateRecord) entity).setUpdator(ThreadContext.getUserId());
    }
    T t = dao.save(entity);
    LOGGER.info("save entity: {}", entity);
    return t;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public T saveAndFlush(T entity) {
    T t = dao.save(entity);
    dao.flush();
    return t;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void delete(ID id) {
    T t = this.get(id);
    this.delete(t);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void delete(final T entity) {
    if (entity == null) {
      return;
    }
    if (entity instanceof DeleteRecord) {
      ((DeleteRecord) entity).markDeleted();
      this.save(entity);
    } else {
      dao.delete(entity);
      LOGGER.info("delete entity: {}", entity);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void deleteInBatch(final ID[] ids) {
    if (ArrayUtils.isEmpty(ids)) {
      return;
    }
    List<T> entities = dao.findAllById(ArrayUtil.asList(ids));
    this.deleteInBatch(entities);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void deleteInBatch(final List<T> entities) {
    if (CollectionUtil.isEmpty(entities)) {
      return;
    }
    if (DeleteRecord.class.isAssignableFrom(this.entityClazz)) {
      for(T entity : entities) {
        ((DeleteRecord) entity).markDeleted();
        this.save(entity);
      }
    } else {
      dao.deleteInBatch(entities);
      LOGGER.info("delete entities: {}", entities);
    }
  }

  public List<T> getList(SearchRequest searchRequest) {
    Specification<T> spec = DynamicSpecification.bySearchRequest(searchRequest, entityClazz);
    return dao.findAll(spec, searchRequest.getSort());
  }

  public Page<T> getPage(SearchRequest searchRequest) {
    Specification<T> spec = DynamicSpecification.bySearchRequest(searchRequest, entityClazz);
    return dao.findAll(spec, searchRequest.getPage());
  }

  public Long count(SearchRequest searchRequest) {
    Specification<T> spec = DynamicSpecification.bySearchRequest(searchRequest, entityClazz);
    return dao.count(spec);
  }

  @Autowired
  public void setJpaDao(JpaDao<T, ID> jpaDao) {
    this.dao = jpaDao;
  }

  @Autowired
  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

}
