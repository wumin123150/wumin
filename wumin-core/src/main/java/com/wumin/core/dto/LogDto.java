package com.wumin.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@ApiModel(description = "日志")
public class LogDto {

  private String id;
  @ApiModelProperty(value = "操作描述")
  private String description;
  @ApiModelProperty(value = "操作用户")
  private String username;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "操作时间")
  private Date logTime;
  @ApiModelProperty(value = "消耗时间")
  private Integer spendTime;
  @ApiModelProperty(value = "IP地址")
  private String ip;
  @ApiModelProperty(value = "URL")
  private String url;
  @ApiModelProperty(value = "请求类型")
  private String method;
  @ApiModelProperty(value = "用户标识")
  private String userAgent;
  @ApiModelProperty(value = "请求参数")
  private String parameter;
  @ApiModelProperty(value = "响应结果")
  private String result;

}
