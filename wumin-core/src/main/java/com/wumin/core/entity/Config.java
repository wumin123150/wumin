package com.wumin.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统配置
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Config")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Config extends IdEntity {

  private String code;//键
  private String value;//值
  private String name;//名称
  private Integer type;//类型(0:Input输入框,1:Textarea文本框,2:Radio单选框,3:Checkbox多选框,4:Select单选框,5:Select多选框)
  private String params;//参数
  private Boolean editable = true;//是否可修改 (0:不可修改,1:可修改)
  private String remark;//备注

  public Config(Long id) {
    this.id = id;
  }

}
