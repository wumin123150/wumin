package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Permission;

public interface PermissionDao extends JpaDao<Permission, Long> {

	Permission findByCode(String code);

}
