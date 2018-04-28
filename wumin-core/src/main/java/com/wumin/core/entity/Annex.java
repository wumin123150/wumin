package com.wumin.core.entity;

import com.wumin.common.entity.UuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 附件
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "Core_Annex")
@SuppressWarnings("serial")
public class Annex extends UuidEntity {

  public static final String PATH_TEMP = "/temp";
  public static final String OBJECT_TYPE_TEMP = "temp";

  private String objectId;//对象ID
  private String objectType;//对象类型
  private String path;//存储路径
  private String name;//文件名称
  private String type;//文件类型
  private Date createTime;//创建时间
  private Long creator;//创建人

  public String getRequestURI() {
    return "/annex" + path;
  }

}
