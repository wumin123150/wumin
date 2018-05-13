package com.wumin.core.dto;

import com.wumin.common.collection.CollectionExtractor;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.FieldsMapper;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.UserGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "用户组")
public class UserGroupDto {

  private Long id;
  @ApiModelProperty(value = "代码", required = true)
  @NotBlank(message = "代码不能为空")
  @Size(max = 64, message = "代码最大长度为64位")
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称最大长度为64位")
  private String name;
  @ApiModelProperty(value = "类型(0:公司,1:部门,2:小组,3:其他)", allowableValues = "0,1,2,3", required = true)
  private Integer type;
  @ApiModelProperty(value = "负责人ID")
  private Long directorId;
  @ApiModelProperty(value = "父ID")
  private Long parentId;
  @ApiModelProperty(value = "备注")
  @Size(max = 128, message = "备注最大长度为128位")
  private String remark;
  @ApiModelProperty(value = "角色ID")
  private List<Long> roles = ListUtil.newArrayList();

  static {
    BeanMapper.registerClassMap(UserGroup.class, UserGroupDto.class, new FieldsMapper<UserGroup, UserGroupDto>() {
      @Override
      public void map(UserGroup userGroup, UserGroupDto userGroupDto) {
        if(userGroup.getDirector() != null) {
          userGroupDto.setDirectorId(userGroup.getDirector().getId());
        }
        if(ListUtil.isNotEmpty(userGroup.getRoleList())) {
          userGroupDto.setRoles(CollectionExtractor.extractToList(userGroup.getRoleList(), "id"));
        }
      }

      @Override
      public void reverseMap(UserGroupDto userGroupDto, UserGroup userGroup) {
        if(userGroupDto.getDirectorId() != null) {
          userGroup.setDirector(new BaseUser(userGroupDto.getDirectorId()));
        } else {
          userGroup.setDirector(null);
        }
        if(ListUtil.isNotEmpty(userGroupDto.getRoles())) {
          userGroup.setRoleList(CollectionExtractor.injectToList(userGroupDto.getRoles(), Role.class));
        } else {
          userGroup.setRoleList(ListUtil.<Role>emptyList());
        }
      }
    });
  }

}
