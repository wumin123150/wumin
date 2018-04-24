package com.wumin.common.entity;

import java.util.Date;

public interface CreateRecord {

  public Date getCreateTime();

  public void setCreateTime(Date createTime);

  public Long getCreator();

  public void setCreator(Long creator);

  public boolean isCreated();

}
