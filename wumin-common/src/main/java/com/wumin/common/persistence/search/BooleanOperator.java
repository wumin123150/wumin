package com.wumin.common.persistence.search;

public enum BooleanOperator {

  AND("并且"), OR("或者");

  private final String name;

  BooleanOperator(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
