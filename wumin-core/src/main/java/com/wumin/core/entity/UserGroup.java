package com.wumin.core.entity;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.entity.DeleteRecord;
import com.wumin.common.entity.IdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

/**
 * 用户组
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_User_Group")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserGroup extends IdEntity {

  public static final Integer TYPE_COMPAMY = 0;//公司
  public static final Integer TYPE_DEPARTMENT = 1;//部门
  public static final Integer TYPE_GROUP = 2;//小组
  public static final Integer TYPE_OTHER = 3;//其他

  public static final String PATH_SPLIT = "/";

  private String code;//代码
  private String name;//名称
  private Integer type;//类型(0:公司,1:部门,2:小组,3:其他)
  private Integer grade;//层级
  @ManyToOne
  @JoinColumn(name="director")
  private BaseUser director;//负责人
  private String path;//路径
  private Long parentId;//父ID
  private String remark;//备注
  private Boolean delFlag;//删除标志(0:正常,1:停用)

  public UserGroup(Long id) {
    this.id = id;
  }

  // 多对多定义
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "Core_Group_Role", joinColumns = {@JoinColumn(name = "group_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
  // Fecth策略定义
  @Fetch(FetchMode.SUBSELECT)
  // 集合按id排序
  @OrderBy("id ASC")
  // 缓存策略
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private List<Role> roleList = ListUtil.newArrayList();

  public void markDeleted() {
    delFlag = true;
  }

}
