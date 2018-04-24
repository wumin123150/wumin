package com.wumin.common.entity;

import java.util.Date;

public interface UpdateRecord {

  public Date getUpdateTime();

  public void setUpdateTime(Date updateTime);

  public Long getUpdator();

  public void setUpdator(Long updator);

}
