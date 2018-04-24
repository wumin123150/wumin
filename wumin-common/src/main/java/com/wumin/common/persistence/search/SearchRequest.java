package com.wumin.common.persistence.search;

import com.wumin.common.collection.CollectionUtil;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.JqGridRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SearchRequest {

  protected BooleanOperator operator = BooleanOperator.AND;
  private final List<SearchFilter> searchFilters = ListUtil.newArrayList();
  private Pageable page;
  private Sort sort;
  private boolean delFlagFilter = true;

  public SearchRequest() {

  }

  public SearchRequest(final PageQuery pageQuery, String... searchProperty) {
    this(pageQuery, true, searchProperty);
  }

  public SearchRequest(final PageQuery pageQuery, final Boolean delFlagFilter, String... searchProperty) {
    this(pageQuery, null, delFlagFilter, searchProperty);
  }

  public SearchRequest(final PageQuery pageQuery, final Sort defaultSort, String... searchProperty) {
    this(pageQuery, defaultSort, true, searchProperty);
  }

  public SearchRequest(final PageQuery pageQuery, final Sort defaultSort, final boolean delFlagFilter, String... searchProperty) {
    this.page = pageQuery.buildPageRequest(defaultSort);
    this.sort = this.page.getSort();
    this.delFlagFilter = delFlagFilter;

    if(searchProperty != null && StringUtils.isNotEmpty(pageQuery.getSearchKey())) {
      List<SearchFilter> searchFilters = ListUtil.newArrayList();
      for (int i = 0; i < searchProperty.length; i++) {
        searchFilters.add(SimpleFilter.contain(searchProperty[i], pageQuery.getSearchKey()));
      }
      addOrSearchFilters(searchFilters);
    }
  }

  public SearchRequest(final JqGridFilter jqGridFilter, final PageQuery pageQuery) {
    this.page = pageQuery.buildPageRequest(null);

    toSearchFilters(jqGridFilter);
  }

  public SearchRequest(final JqGridFilter jqGridFilter, final PageQuery pageQuery, final Sort defaultSort) {
    this.page = pageQuery.buildPageRequest(defaultSort);
    this.sort = this.page.getSort();

    toSearchFilters(jqGridFilter);
  }

  public SearchRequest(final Map<String, Object> searchParams) {
    this(searchParams, null, null);
  }

  public SearchRequest(final Map<String, Object> searchParams, final Pageable page) {
    this(searchParams, page, null);
  }

  public SearchRequest(final Map<String, Object> searchParams, final Sort sort) {
    this(searchParams, null, sort);
  }

  public SearchRequest(final Map<String, Object> searchParams, final Pageable page, final Sort sort) {
    toSearchFilters(searchParams);
    merge(sort, page);
  }

  private void toSearchFilters(final Map<String, Object> searchParams) {
    if (searchParams == null || searchParams.size() == 0) {
      return;
    }
    for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // 拆分operator与fieldAttribute
      String[] names = StringUtils.split(key, "_");
      if (names.length != 2) {
        throw new IllegalArgumentException(key + " is not a valid search filter name");
      }
      String propertyName = names[1];
      SearchOperator operator = SearchOperator.valueOf(names[0].toUpperCase());

      if (operator == SearchOperator.EQ || operator == SearchOperator.NE || operator == SearchOperator.LT
        || operator == SearchOperator.LE || operator == SearchOperator.GT || operator == SearchOperator.GE
        || operator == SearchOperator.SW || operator == SearchOperator.SN || operator == SearchOperator.EW
        || operator == SearchOperator.EN || operator == SearchOperator.CN || operator == SearchOperator.NN
        || operator == SearchOperator.IN || operator == SearchOperator.NI) {
        if (value == null || (value instanceof String && StringUtils.isEmpty((String) value))) {
          continue;
        }
      }

      if (operator == SearchOperator.IN || operator == SearchOperator.NI) {
        if (value instanceof Object[]) {
          value = Arrays.asList((Object[]) value);
        }

        if(value instanceof String) {
          value = Arrays.asList(StringUtils.split((String) value, ", "));
        }

        value = ListUtil.newArrayList(value);
      }
      searchFilters.add(new SimpleFilter(propertyName, operator, value));
    }
  }

  private void toSearchFilters(final JqGridFilter jqGridFilter) {
    if (jqGridFilter != null) {
      operator = StringUtils.equalsIgnoreCase(jqGridFilter.getGroupOp(), "or") ? BooleanOperator.OR : BooleanOperator.AND;

      for (JqGridRule jqGridRule : jqGridFilter.getRules()) {
        String propertyName = jqGridRule.getField();
        Object value = jqGridRule.getData();
        SearchOperator operator = SearchOperator.valueOf(jqGridRule.getOp().toUpperCase());

        if (operator == SearchOperator.EQ || operator == SearchOperator.NE || operator == SearchOperator.LT
          || operator == SearchOperator.LE || operator == SearchOperator.GT || operator == SearchOperator.GE
          || operator == SearchOperator.SW || operator == SearchOperator.SN || operator == SearchOperator.EW
          || operator == SearchOperator.EN || operator == SearchOperator.CN || operator == SearchOperator.NC
          || operator == SearchOperator.IN || operator == SearchOperator.NI) {
          if (value == null || (value instanceof String && StringUtils.isEmpty((String) value))) {
            continue;
          }
        }

        if (operator == SearchOperator.IN || operator == SearchOperator.NI) {
          value = Arrays.asList(StringUtils.split((String) value, ","));
        }

        searchFilters.add(new SimpleFilter(propertyName, operator, value));
      }
    }
  }

  public SearchRequest addSearchParams(Map<String, Object> searchParams) {
    toSearchFilters(searchParams);
    return this;
  }

  public SearchRequest addSearchFilters(Collection<? extends SearchFilter> searchFilters) {
    if (CollectionUtils.isEmpty(searchFilters)) {
      return this;
    }
    this.searchFilters.addAll(searchFilters);
    return this;
  }

  public SearchRequest addOrSearchFilters(Collection<? extends SearchFilter> searchFilters) {
    if (CollectionUtils.isEmpty(searchFilters)) {
      return this;
    }
    this.searchFilters.add(new CompoundFilter(BooleanOperator.OR, searchFilters));
    return this;
  }

  public SearchRequest addAndSearchFilters(Collection<? extends SearchFilter> searchFilters) {
    if (CollectionUtils.isEmpty(searchFilters)) {
      return this;
    }
    this.searchFilters.add(new CompoundFilter(BooleanOperator.AND, searchFilters));
    return this;
  }

  public SearchRequest addEqualTo(String fieldName, Object value) {
    if(value != null) {
      searchFilters.add(SimpleFilter.equalTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addEqualToNotEmpty(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.equalTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addNotEqualTo(String fieldName, Object value) {
    if(value != null) {
      searchFilters.add(SimpleFilter.notEqualTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addNotEqualToNotEmpty(String fieldName, Object value) {
    if(value != null) {
      searchFilters.add(SimpleFilter.notEqualTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addLessThan(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.lessThan(fieldName, value));
    }
    return this;
  }

  public SearchRequest addLessThanOrEqualTo(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.lessThanOrEqualTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addGreaterThan(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.greaterThan(fieldName, value));
    }
    return this;
  }

  public SearchRequest addGreaterOrEqualTo(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.greaterOrEqualTo(fieldName, value));
    }
    return this;
  }

  public SearchRequest addStartWith(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.startWith(fieldName, value));
    }
    return this;
  }

  public SearchRequest addNotStartWith(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.notStartWith(fieldName, value));
    }
    return this;
  }

  public SearchRequest addEndWith(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.endWith(fieldName, value));
    }
    return this;
  }

  public SearchRequest addNotEndWith(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.notEndWith(fieldName, value));
    }
    return this;
  }

  public SearchRequest addContain(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.contain(fieldName, value));
    }
    return this;
  }

  public SearchRequest addNotContain(String fieldName, Object value) {
    if (!(value == null || (value instanceof String && StringUtils.isEmpty((String) value)))) {
      searchFilters.add(SimpleFilter.notContain(fieldName, value));
    }
    return this;
  }

  public SearchRequest addIsNull(String fieldName) {
    searchFilters.add(SimpleFilter.isNull(fieldName));
    return this;
  }

  public SearchRequest addIsNotNull(String fieldName) {
    searchFilters.add(SimpleFilter.isNotNull(fieldName));
    return this;
  }

  public SearchRequest addIn(String fieldName, Collection<Object> values) {
    if(CollectionUtil.isNotEmpty(values)) {
      searchFilters.add(SimpleFilter.in(fieldName, values));
    }
    return this;
  }

  public SearchRequest addNotIn(String fieldName, Collection<Object> values) {
    if(CollectionUtil.isNotEmpty(values)) {
      searchFilters.add(SimpleFilter.notIn(fieldName, values));
    }
    return this;
  }

  public SearchRequest setOperator(final BooleanOperator operator) {
    this.operator = operator;
    return this;
  }

  public SearchRequest setPage(final Pageable page) {
    merge(sort, page);
    return this;
  }

  public SearchRequest setPage(int pageNo, int pageSize) {
    merge(sort, new PageRequest(pageNo, pageSize));
    return this;
  }

  public SearchRequest addSort(final Sort sort) {
    merge(sort, page);
    return this;
  }

  public SearchRequest addSort(final Sort.Direction direction, final String property) {
    merge(new Sort(direction, property), page);
    return this;
  }

  public void openDelFlagFilter() {
    this.delFlagFilter = true;
  }

  public void closeDelFlagFilter() {
    this.delFlagFilter = false;
  }

  public BooleanOperator getOperator() {
    return operator;
  }

  public List<SearchFilter> getSearchFilters() {
    return searchFilters;
  }

  public boolean hasDelFlagFilter() {
    return delFlagFilter;
  }

  public boolean hasSearchFilter() {
    return searchFilters.size() > 0;
  }

  public boolean hashSort() {
    return this.sort != null && this.sort.iterator().hasNext();
  }

  public boolean hasPageable() {
    return this.page != null && this.page.getPageSize() > 0;
  }

  public void removeSort() {
    this.sort = null;
    if (this.page != null) {
      this.page = new PageRequest(page.getPageNumber(), page.getPageSize(), null);
    }
  }

  public void removePageable() {
    this.page = null;
  }

  public Sort getSort() {
    return sort;
  }

  public Pageable getPage() {
    return page;
  }

  private void merge(Sort sort, Pageable page) {
    if (sort == null) {
      sort = this.sort;
    }
    if (page == null) {
      page = this.page;
    }

    //合并排序
    if (sort == null) {
      this.sort = page != null ? page.getSort() : null;
    } else {
      this.sort = (page != null ? sort.and(page.getSort()) : sort);
    }
    //把排序合并到page中
    if (page != null) {
      this.page = new PageRequest(page.getPageNumber(), page.getPageSize(), this.sort);
    } else {
      this.page = null;
    }
  }

  private void replace(Sort sort, Pageable page) {
    if (sort == null) {
      sort = this.sort;
    }
    if (page == null) {
      page = this.page;
    }

    if (sort == null) {
      this.sort = page != null ? page.getSort() : null;
    } else {
      this.sort = (page != null ? sort.and(page.getSort()) : sort);
    }
    //把排序合并到page中
    if (page != null) {
      this.page = new PageRequest(page.getPageNumber(), page.getPageSize(), this.sort);
    } else {
      this.page = null;
    }
  }

}
