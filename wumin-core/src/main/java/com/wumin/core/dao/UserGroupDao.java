package com.wumin.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.UserGroup;

public interface UserGroupDao extends JpaDao<UserGroup, Long> {

  @Query("FROM UserGroup entity WHERE entity.code = :code")
  UserGroup findByCode(@Param("code") String code);

  @Query("FROM UserGroup entity WHERE entity.delFlag = false ORDER BY entity.path ASC")
  List<UserGroup> findAll();

  @Query("SELECT entity FROM UserGroup entity WHERE entity.grade=1 AND entity.delFlag = false ORDER BY entity.code ASC")
  List<UserGroup> findTop();

  @Query("SELECT entity FROM UserGroup entity WHERE entity.path LIKE :path AND entity.delFlag = false ORDER BY entity.path ASC")
  List<UserGroup> findByPath(@Param("path") String path);

  @Query("SELECT entity FROM UserGroup entity WHERE entity.parentId=:parentId AND entity.delFlag = false ORDER BY entity.code ASC")
  List<UserGroup> findChildrenByParent(@Param("parentId") Long parentId);

}
