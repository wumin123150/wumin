package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Role;

public interface RoleDao extends JpaDao<Role, Long> {

	Role findByCode(String code);

}
