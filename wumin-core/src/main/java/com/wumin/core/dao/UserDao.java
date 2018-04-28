package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.User;

public interface UserDao extends JpaDao<User, Long> {

	User findByOpenId(String openId);
	
}
