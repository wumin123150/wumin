package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.core.dao.BaseUserDao;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.QUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户管理业务类.
 *
 * @author walker
 */
@Service
public class BaseUserService extends JpaServiceImpl<BaseUser, Long> {

  @Autowired
  private BaseUserDao baseUserDao;

  public BaseUser getByLoginName(String loginName) {
    Optional<BaseUser> result = baseUserDao.findOne(QUser.user.loginName.equalsIgnoreCase(loginName));
    return result.isPresent() ? result.get() : null;
  }

  public boolean existLoginName(Long id, String loginName) {
    BaseUser user = getByLoginName(loginName);
    if (user == null || (id != null && id.equals(user.getId()))) {
      return false;
    } else {
      return true;
    }
  }

}
