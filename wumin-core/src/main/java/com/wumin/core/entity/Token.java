package com.wumin.core.entity;

import com.wumin.common.entity.UuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 访问令牌，用于移动设备
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "Core_Token")
@SuppressWarnings("serial")
public class Token extends UuidEntity {

  public static final int STATUS_ENABLE = 0;
  public static final int STATUS_DISABLE = 1;

  public static final int DEFAULT_EXPIRE = 5 * 24;// 5天
  public static final int DEFAULT_RENEW = 4 * 24;// 4天

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;//用户
  private Date expireTime;//过期时间
  private String terminal;//终端
  private String remark;//备注
  private Integer status;//状态(0:有效,1:失效)
  private Date createTime;//创建时间

  @Transient
  public boolean isEnabled() {
    return STATUS_ENABLE == status && expireTime.compareTo(new Date()) > 0;
  }

  @Transient
  public boolean renew(Date date) {
    return STATUS_ENABLE == status && expireTime.compareTo(date) < 0;
  }

}
