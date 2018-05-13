package com.wumin.core.dto;

import com.wumin.common.collection.CollectionExtractor;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.FieldsMapper;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.User;
import com.wumin.core.entity.UserGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "用户")
public class UserDto {

  private Long id;
  @ApiModelProperty(value = "账号", required = true)
  @NotBlank(message = "账号不能为空")
  @Size(max = 64, message = "账号最大长度为64位")
  private String loginName;
  @ApiModelProperty(value = "密码")
  private String password;
  @ApiModelProperty(value = "姓名", required = true)
  @NotBlank(message = "姓名不能为空")
  @Size(max = 64, message = "姓名最大长度为64位")
  private String name;
  @ApiModelProperty(value = "性别(0:保密,1:男,2:女)")
  private Integer sex;
  @ApiModelProperty(value = "出生日期")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private Date birthday;
  @ApiModelProperty(value = "手机")
  @Size(max = 16, message = "手机最大长度为16位")
  private String mobile;
  @ApiModelProperty(value = "邮箱")
  @Size(max = 64, message = "邮箱最大长度为64位")
  private String email;
  @ApiModelProperty(value = "状态(I:未激活,A:正常,E:过期,L:锁定,T:终止)", allowableValues = "I,A,E,L,T", required = true)
  @NotBlank(message = "状态不能为空")
  private String status;
  @ApiModelProperty(value = "头像ID")
  private String avatarId;
  @ApiModelProperty(value = "角色")
  private List<Long> roles;
  @ApiModelProperty(value = "机构")
  private List<Long> groups;

  static {
    BeanMapper.registerClassMap(User.class, UserDto.class, ListUtil.newArrayList("password"), new FieldsMapper<User, UserDto>() {
      @Override
      public void map(User user, UserDto userDto) {
        if(ListUtil.isNotEmpty(user.getRoleList())) {
          userDto.setRoles(CollectionExtractor.extractToList(user.getRoleList(), "id"));
        }
        if(ListUtil.isNotEmpty(user.getGroupList())) {
          userDto.setGroups(CollectionExtractor.extractToList(user.getGroupList(), "id"));
        }
      }

      @Override
      public void reverseMap(UserDto userDto, User user) {
        if(StringUtils.isNotEmpty(userDto.getPassword())) {
          user.setPlainPassword(user.getPassword());
        }
        if(ListUtil.isNotEmpty(userDto.getRoles())) {
          user.setRoleList(CollectionExtractor.injectToList(userDto.getRoles(), Role.class));
        } else {
          user.setRoleList(ListUtil.<Role>emptyList());
        }
        if(ListUtil.isNotEmpty(userDto.getGroups())) {
          user.setGroupList(CollectionExtractor.injectToList(userDto.getGroups(), UserGroup.class));
        } else {
          user.setGroupList(ListUtil.<UserGroup>emptyList());
        }
      }
    });
  }

}
