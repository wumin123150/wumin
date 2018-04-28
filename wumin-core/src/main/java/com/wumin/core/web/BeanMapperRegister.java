package com.wumin.core.web;

import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.FieldsMapper;
import com.wumin.core.dto.RoleDto;
import com.wumin.core.dto.UserDto;
import com.wumin.core.dto.UserGroupDto;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.User;
import com.wumin.core.entity.UserGroup;

public class BeanMapperRegister {

  public void init() {
    BeanMapper.registerClassMap(User.class, UserDto.class, new FieldsMapper<User, UserDto>() {
      @Override
      public void map(User user, UserDto userDto) {
        userDto.setRoles(BeanMapper.mapList(user.getRoleList(), Role.class, RoleDto.class));
        userDto.setGroups(BeanMapper.mapList(user.getGroupList(), UserGroup.class, UserGroupDto.class));
      }
    });
  }

}
