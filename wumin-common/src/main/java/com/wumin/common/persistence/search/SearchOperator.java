package com.wumin.common.persistence.search;


public enum SearchOperator {

  /**
   * 属性比较类型.
   * 'equal', 'not equal', 'less', 'less or equal', 'greater', 'greater or equal', 'starts with', 'does not starts with', 'is in', 'is not in', 'ends with', 'does not end with', 'contains', 'does not contain'
   */
  EQ("等于"), NE("不等于"), LT("小于"), LE("小于等于"), GT("大于"), GE("大于等于"), SW("前缀模糊匹配"), SN("前缀模糊不匹配"), IN("包含"), NI("不包含")
  , EW("后缀模糊匹配"), EN("后缀模糊不匹配"), CN("模糊匹配"), NC("不匹配"), NU("空"), NN("非空");

  private final String name;

  SearchOperator(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
