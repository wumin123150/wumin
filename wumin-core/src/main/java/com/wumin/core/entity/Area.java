package com.wumin.core.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 省市县.
 *
 * @author walker
 */
@Data
@NoArgsConstructor
@ApiModel(description = "地区")
@Entity
@Table(name = "Core_Area")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Area implements Serializable {

  public static final String TYPE_SHEN = "1";//省
  public static final String TYPE_ZHI_SHI = "2";//直辖市
  public static final String TYPE_DI_SHI = "3";//地级市、地区、自治州、省直辖县、自治县
  public static final String TYPE_XIAN_SHI = "4";//县级市
  public static final String TYPE_XIAN = "5";//县、旗
  public static final String TYPE_QU = "6";//区

  @Id
  private String id;
  @ApiModelProperty(value = "编码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "全称")
  private String fullName;
  @ApiModelProperty(value = "层级")
  private Integer grade;
  @ApiModelProperty(value = "类型(1:省,2:直辖市,3:地级市,4:县级市,5:县,6:区)")
  private String type;
  @ApiModelProperty(value = "根ID")
  private String rootId;
  @ApiModelProperty(value = "父ID")
  private String parentId;

  public Area(String id) {
    this.id = id;
  }

}
