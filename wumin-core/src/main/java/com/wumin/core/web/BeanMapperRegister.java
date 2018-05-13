package com.wumin.core.web;

import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.FieldsMapper;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.User;
import com.wumin.core.entity.UserGroup;
import com.wumin.core.vo.RoleVo;
import com.wumin.core.vo.UserGroupVo;
import com.wumin.core.vo.UserVo;

public class BeanMapperRegister {

  public void init() {
    BeanMapper.registerClassMap(User.class, UserVo.class, new FieldsMapper<User, UserVo>() {
      @Override
      public void map(User user, UserVo userVo) {
        userVo.setRoles(BeanMapper.mapList(user.getRoleList(), Role.class, RoleVo.class));
        userVo.setGroups(BeanMapper.mapList(user.getGroupList(), UserGroup.class, UserGroupVo.class));
      }
    });
  }

}
