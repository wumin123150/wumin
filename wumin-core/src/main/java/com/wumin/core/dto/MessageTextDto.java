package com.wumin.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wumin.core.entity.BaseUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "消息内容")
public class MessageTextDto {

  private String id;
  @ApiModelProperty(value = "内容")
  private String content;
  @ApiModelProperty(value = "发送人")
  private BaseUser sender;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "创建时间")
  private Date createTime;

}
