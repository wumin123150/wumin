package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.DataDictionary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataDictionaryDao extends JpaDao<DataDictionary, Long> {

  @Query("SELECT entity FROM DataDictionary entity WHERE entity.typeId=:typeId AND entity.code=:code")
  DataDictionary findByTypeAndCode(@Param("typeId") Long typeId, @Param("code") String code);

  @Query("SELECT entity FROM DataDictionary entity WHERE entity.typeId=:typeId ORDER BY entity.priority ASC")
  List<DataDictionary> findByType(@Param("typeId") Long typeId);

  @Query("SELECT entity FROM DataDictionary entity WHERE entity.typeId=:typeId AND entity.grade=:grade ORDER BY entity.priority ASC")
  List<DataDictionary> findByTypeAndGrade(@Param("typeId") Long typeId, @Param("grade") Integer grade);

  @Query("SELECT entity FROM DataDictionary entity WHERE entity.parentId=:parentId ORDER BY entity.priority ASC")
  List<DataDictionary> findChildrenByParent(@Param("parentId") Long parentId);

}
