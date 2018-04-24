package com.wumin.common.entity;

public interface DeleteRecord {

  public Boolean getDelFlag();

  public void setDelFlag(Boolean delFlag);

  public void markDeleted();

  public boolean isCreated();

}
