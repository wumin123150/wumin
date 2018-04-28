package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.DataType;

public interface DataTypeDao extends JpaDao<DataType, Long> {

	DataType findByCode(String code);

}
