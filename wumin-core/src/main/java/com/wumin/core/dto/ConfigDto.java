package com.wumin.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "系统配置")
public class ConfigDto {

  private Long id;
  @ApiModelProperty(value = "名称", required = true)
  private String name;
  @ApiModelProperty(value = "键", required = true)
  private String code;
  @ApiModelProperty(value = "值", required = true)
  private String value;

}
