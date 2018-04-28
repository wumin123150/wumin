package com.wumin.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wumin.core.entity.BaseUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "消息")
public class MessageDto {

  private String id;
  @ApiModelProperty(value = "内容")
  private String content;
  @ApiModelProperty(value = "发送人")
  private BaseUser sender;
  @ApiModelProperty(value = "接收人")
  private BaseUser receiver;
  @ApiModelProperty(value = "发送状态(0:发送中,1:已发送,2:发送失败)")
  private Integer sendStatus;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "发送时间")
  private Date sendTime;
  @ApiModelProperty(value = "阅读状态(0:未读,1:已读)")
  private Integer readStatus;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "阅读时间")
  private Date readTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty(value = "创建时间")
  private Date createTime;

}
