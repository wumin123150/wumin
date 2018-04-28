package com.wumin.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "附件")
public class AnnexDto {

  private String id;
  @ApiModelProperty(value = "对象ID")
  private String objectId;
  @ApiModelProperty(value = "对象类型")
  private String objectType;
  @ApiModelProperty(value = "存储路径")
  private String path;
  @ApiModelProperty(value = "文件名称")
  private String name;
  @ApiModelProperty(value = "文件类型")
  private String type;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "创建时间")
  private Date createTime;

}
