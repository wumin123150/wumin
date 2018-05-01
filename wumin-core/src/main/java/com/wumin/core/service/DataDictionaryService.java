package com.wumin.core.service;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.DataDictionaryDao;
import com.wumin.core.entity.DataDictionary;
import com.wumin.core.entity.QDataDictionary;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典管理业务类.
 *
 * @author walker
 */
@Service
@Transactional
public class DataDictionaryService extends JpaServiceImpl<DataDictionary, Long> {

  @Autowired
  private DataDictionaryDao dataDictionaryDao;

  public DataDictionary getByTypeAndCode(Long typeId, String code) {
    return dataDictionaryDao.findByTypeAndCode(typeId, code);
//    QDataDictionary qDataDictionary = QDataDictionary.dataDictionary;
//    return queryFactory.selectFrom(qDataDictionary).where(qDataDictionary.typeId.eq(typeId).and(qDataDictionary.code.eq(code))).fetchOne();
  }

  public List<DataDictionary> findByType(Long typeId) {
    return dataDictionaryDao.findByType(typeId);
  }

  public List<DataDictionary> findByTypeAndGrade(Long typeId, Integer grade) {
    return dataDictionaryDao.findByTypeAndGrade(typeId, grade);
  }

  public List<DataDictionary> findChildrenByParent(Long id) {
    return dataDictionaryDao.findChildrenByParent(id);
  }

  public List<DataDictionary> findChildrenByTypeAndCode(Long typeId, String code) {
    DataDictionary parent = getByTypeAndCode(typeId, code);
    if (parent != null) {
      return dataDictionaryDao.findChildrenByParent(parent.getId());
    }
    return ListUtil.newArrayList();
  }

  public Page<DataDictionary> getPage(final String searchKey, final Long typeId, final Long parentId, Pageable page) {
    QDataDictionary qDataDictionary = QDataDictionary.dataDictionary;
    List<BooleanExpression> expressions = new ArrayList<>();

    expressions.add(qDataDictionary.typeId.eq(typeId));

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qDataDictionary.code.like(StringUtils.trimToEmpty(searchKey))
        .or(qDataDictionary.name.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (parentId == null) {
      expressions.add(qDataDictionary.parentId.isNull());
    } else {
      expressions.add(qDataDictionary.parentId.eq(parentId));
    }

    return dataDictionaryDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public boolean existCode(Long id, Long typeId, String code) {
    DataDictionary dataDictionary = getByTypeAndCode(typeId, code);
    if (dataDictionary == null || (id != null && id.equals(dataDictionary.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
