package com.wumin.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 日志
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "Core_Log")
@SuppressWarnings("serial")
public class Log extends UuidEntity {

  private String description;//操作描述
  private String username;//操作用户
  private Date logTime;//操作时间
  private Integer spendTime;//消耗时间
  private String ip;//IP地址
  private String url;//URL
  private String method;//请求类型
  private String userAgent;//用户标识
  private String parameter;//请求参数
  private String result;//响应结果

}
