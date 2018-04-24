package com.wumin.core.entity;

import com.foomei.common.collection.CollectionExtractor;
import com.foomei.common.collection.ListUtil;
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
 * 角色.
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Role")
@SuppressWarnings("serial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends IdEntity {
  
  private String code;//代码
  private String name;//名称

  @ManyToMany
  @JoinTable(name = "Core_Role_Permission", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "permissionId")})
  @Fetch(FetchMode.SUBSELECT)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private List<Permission> permissionList = ListUtil.newArrayList(); // 有序的关联对象集合

  public Role(Long id) {
    this.id = id;
  }

  @Transient
  public List<String> getPermissions() {
    return CollectionExtractor.extractToList(permissionList, "code");
  }

}
