package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Config;

public interface ConfigDao extends JpaDao<Config, Long> {

	Config findByCode(String code);

}
