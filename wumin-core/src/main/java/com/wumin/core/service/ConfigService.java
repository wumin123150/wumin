package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.ConfigDao;
import com.wumin.core.entity.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统配置管理业务类.
 *
 * @author walker
 */
@Service
public class ConfigService extends JpaServiceImpl<Config, Long> {

  @Autowired
  private ConfigDao configDao;

  public Config getByCode(String code) {
    return configDao.findByCode(code);
  }

  public boolean existCode(Long id, String code) {
    Config config = getByCode(code);
    if (config == null || (id != null && id.equals(config.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
