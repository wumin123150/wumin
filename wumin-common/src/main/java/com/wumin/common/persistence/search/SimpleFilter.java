package com.wumin.common.persistence.search;

import java.util.Collection;

public class SimpleFilter implements SearchFilter {

  private String propertyName;
  private Object value;
  private SearchOperator operator;

  public SimpleFilter(String propertyName, SearchOperator operator, Object value) {
    this.propertyName = propertyName;
    this.operator = operator;
    this.value = value;
  }

  public static SimpleFilter equalTo(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.EQ, value);
  }

  public static SimpleFilter notEqualTo(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.NE, value);
  }

  public static SimpleFilter lessThan(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.LT, value);
  }

  public static SimpleFilter lessThanOrEqualTo(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.LE, value);
  }

  public static SimpleFilter greaterThan(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.GT, value);
  }

  public static SimpleFilter greaterOrEqualTo(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.GE, value);
  }

  public static SimpleFilter startWith(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.SW, value);
  }

  public static SimpleFilter notStartWith(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.SN, value);
  }

  public static SimpleFilter endWith(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.EW, value);
  }

  public static SimpleFilter notEndWith(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.EN, value);
  }

  public static SimpleFilter contain(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.CN, value);
  }

  public static SimpleFilter notContain(String propertyName, Object value) {
    return new SimpleFilter(propertyName, SearchOperator.NC, value);
  }

  public static SimpleFilter isNull(String propertyName) {
    return new SimpleFilter(propertyName, SearchOperator.NU, null);
  }

  public static SimpleFilter isNotNull(String propertyName) {
    return new SimpleFilter(propertyName, SearchOperator.NN, null);
  }

  public static SimpleFilter in(String propertyName, Collection<Object> values) {
    return new SimpleFilter(propertyName, SearchOperator.IN, values);
  }

  public static SimpleFilter notIn(String propertyName, Collection<Object> values) {
    return new SimpleFilter(propertyName, SearchOperator.NI, values);
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public SearchOperator getOperator() {
    return operator;
  }

  public void setOperator(SearchOperator operator) {
    this.operator = operator;
  }
}
