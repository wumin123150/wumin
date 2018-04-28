package com.wumin.core.service;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.UserGroupDao;
import com.wumin.core.entity.QUserGroup;
import com.wumin.core.entity.UserGroup;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户组管理业务类.
 *
 * @author walker
 */
@Service
public class UserGroupService extends JpaServiceImpl<UserGroup, Long> {

  @Autowired
  private UserGroupDao userGroupDao;

  public UserGroup getByCode(String code) {
    return userGroupDao.findByCode(code);
  }

  public List<UserGroup> getAll() {
    return userGroupDao.findAll();
  }

  public List<UserGroup> findTop() {
    return userGroupDao.findTop();
  }

  public List<UserGroup> findTreeByPath(String path) {
    return userGroupDao.findByPath(path + UserGroup.PATH_SPLIT + "%");
  }

  public List<UserGroup> findChildrenByParent(Long id) {
    return userGroupDao.findChildrenByParent(id);
  }

  public List<UserGroup> findChildrenByCode(String code) {
    UserGroup parent = getByCode(code);
    if (parent != null) {
      return findChildrenByParent(parent.getId());
    }
    return ListUtil.newArrayList();
  }

  public UserGroup save(UserGroup userGroup) {
    if (userGroup.getId() == null) {
      userGroup.setDelFlag(false);
    }

    String srcPath = userGroup.getPath();
    if (!StringUtils.endsWith(srcPath, UserGroup.PATH_SPLIT + userGroup.getCode())) {
      List<UserGroup> children = findTreeByPath(userGroup.getPath());

      String tarPath = StringUtils.substringBeforeLast(srcPath, UserGroup.PATH_SPLIT) + UserGroup.PATH_SPLIT + userGroup.getCode();
      for (UserGroup child : children) {
        String path = tarPath + StringUtils.substring(child.getPath(), srcPath.length());
        child.setPath(path);
        super.save(child);
      }
      userGroup.setPath(tarPath);
    }

    return super.save(userGroup);
  }

  public Page<UserGroup> getPage(final String searchKey, final Long parentId, Pageable page) {
    QUserGroup qUserGroup = QUserGroup.userGroup;
    List<BooleanExpression> expressions = new ArrayList<>();

    expressions.add(qUserGroup.delFlag.eq(false));

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qUserGroup.code.like(StringUtils.trimToEmpty(searchKey))
        .or(qUserGroup.name.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (parentId == null) {
      expressions.add(qUserGroup.parentId.isNull());
    } else {
      expressions.add(qUserGroup.parentId.eq(parentId));
    }

    return userGroupDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public boolean existCode(Long id, String code) {
    UserGroup userGroup = getByCode(code);
    if (userGroup == null || (id != null && id.equals(userGroup.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
