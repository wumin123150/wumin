package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Annex;

public interface AnnexDao extends JpaDao<Annex, String> {

	Annex findByPath(String path);
	
}
