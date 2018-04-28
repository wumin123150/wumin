package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.DataTypeDao;
import com.wumin.core.entity.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据类型管理业务类.
 *
 * @author walker
 */
@Service
public class DataTypeService extends JpaServiceImpl<DataType, Long> {

  @Autowired
  private DataTypeDao dataTypeDao;

  public DataType getByCode(String code) {
    return dataTypeDao.findByCode(code);
  }

  public boolean existCode(Long id, String code) {
    DataType dataType = getByCode(code);
    if (dataType == null || (id != null && id.equals(dataType.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
