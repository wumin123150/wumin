package com.wumin.core.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.foomei.common.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户基本信息.
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@ApiModel(description = "用户")
@Entity
@Table(name = "Core_User")
@SuppressWarnings("serial")
public class BaseUser extends IdEntity {

  @ApiModelProperty(value = "账号")
  private String loginName;
  @ApiModelProperty(value = "姓名")
  private String name;
  @ApiModelProperty(value = "头像")
  private String avatar;

  public BaseUser(Long id) {
    this.id = id;
  }

  public BaseUser(Long id, String loginName, String name) {
    this.id = id;
    this.loginName = loginName;
    this.name = name;
  }

}