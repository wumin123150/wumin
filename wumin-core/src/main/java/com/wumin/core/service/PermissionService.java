package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.PermissionDao;
import com.wumin.core.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限管理业务类.
 *
 * @author walker
 */
@Service
public class PermissionService extends JpaServiceImpl<Permission, Long> {

  @Autowired
  private PermissionDao permissionDao;

  public Permission getByCode(String code) {
    return permissionDao.findByCode(code);
  }

  public List<Permission> getAll() {
    return permissionDao.findAll(new Sort(Direction.ASC, "priority"));
  }

  public boolean existCode(Long id, String code) {
    Permission permission = getByCode(code);
    if (permission == null || (id != null && id.equals(permission.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
