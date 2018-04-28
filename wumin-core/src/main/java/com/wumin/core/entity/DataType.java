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
 * 数据类型
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Data_Type")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DataType extends IdEntity {

  private String code;//代码
  private String name;//名称
  private Integer grade;//层级
  private Boolean editable = true;//是否可修改 (0:不可修改,1:可修改)
  private String remark;//备注

  public DataType(Long id) {
    this.id = id;
  }

}
