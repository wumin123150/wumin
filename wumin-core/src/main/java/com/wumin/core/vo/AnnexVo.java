package com.wumin.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "附件")
public class AnnexVo {

  private String id;
  @ApiModelProperty(value = "访问路径")
  private String requestURI;

}
