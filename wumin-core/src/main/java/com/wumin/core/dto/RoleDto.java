package com.wumin.core.dto;

import com.wumin.common.collection.CollectionExtractor;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.FieldsMapper;
import com.wumin.core.entity.Permission;
import com.wumin.core.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "角色")
public class RoleDto {

  private Long id;
  @ApiModelProperty(value = "编码", required = true)
  @NotBlank(message = "代码不能为空")
  @Size(max = 64, message = "代码长度必须在1到64位之间")
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称长度必须在1到64位之间")
  private String name;
  @ApiModelProperty(value = "权限")
  private List<Long> permissions;

  static {
    BeanMapper.registerClassMap(Role.class, RoleDto.class, new FieldsMapper<Role, RoleDto>() {
      @Override
      public void map(Role role, RoleDto roleDto) {
        if(ListUtil.isNotEmpty(role.getPermissionList())) {
          roleDto.setPermissions(CollectionExtractor.extractToList(role.getPermissionList(), "id"));
        }
      }

      @Override
      public void reverseMap(RoleDto roleDto, Role role) {
        if(ListUtil.isNotEmpty(roleDto.getPermissions())) {
          role.setPermissionList(CollectionExtractor.injectToList(roleDto.getPermissions(), Permission.class));
        } else {
          role.setPermissionList(ListUtil.<Permission>emptyList());
        }
      }
    });
  }

}
