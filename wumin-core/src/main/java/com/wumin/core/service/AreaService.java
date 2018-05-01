package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.AreaDao;
import com.wumin.core.entity.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 省市县类.
 *
 * @author walker
 */
@Service
@Transactional
public class AreaService extends JpaServiceImpl<Area, String> {

  @Autowired
  private AreaDao areaDao;

  public Area getByCode(String code) {
    return areaDao.findByCode(code);
  }

  public List<Area> findTop() {
    return areaDao.findTop();
  }

  public List<Area> findChildrenByParent(String parentId) {
    return areaDao.findChildrenByParent(parentId);
  }

  public List<Area> findTownByRoot(String rootId) {
    return areaDao.findTownByRoot(rootId);
  }

  public boolean existCode(String id, String code) {
    Area area = getByCode(code);
    if (area == null || (id != null && id.equals(area.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
