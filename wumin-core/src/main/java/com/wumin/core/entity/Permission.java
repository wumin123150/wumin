package com.wumin.core.entity;

import com.wumin.common.entity.IdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 权限
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Permission")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends IdEntity {
//  public static final String PROP_CODE = "code";
//  public static final String PROP_NAME = "name";
//  public static final String PROP_PRIORITY = "priority";

  private String code;//代码
  private String name;//名称
  private Integer priority;//序号

  public Permission(Long id) {
    this.id = id;
  }

}