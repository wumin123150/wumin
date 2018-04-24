package com.wumin.common.persistence.search;

import com.wumin.common.collection.ListUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CompoundFilter implements SearchFilter {

  private BooleanOperator operator;
  private List<SearchFilter> searchFilters = ListUtil.newArrayList();

  public CompoundFilter(BooleanOperator operator) {
    this.operator = operator;
  }

  public CompoundFilter(BooleanOperator operator, SearchFilter... searchFilters) {
    this(operator);
    this.searchFilters.addAll(Arrays.asList(searchFilters));
  }

  public CompoundFilter(BooleanOperator operator, Collection<? extends SearchFilter> searchFilters) {
    this(operator);
    this.searchFilters.addAll(searchFilters);
  }

  public CompoundFilter add(SearchFilter searchFilter) {
    this.searchFilters.add(searchFilter);
    return this;
  }

  public BooleanOperator getOperator() {
    return operator;
  }

  public List<SearchFilter> getSearchFilters() {
    return searchFilters;
  }

}
