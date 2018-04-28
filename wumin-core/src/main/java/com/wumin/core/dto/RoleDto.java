package com.wumin.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "角色")
public class RoleDto {

  private Long id;
  @ApiModelProperty(value = "编码", required = true)
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  private String name;

}