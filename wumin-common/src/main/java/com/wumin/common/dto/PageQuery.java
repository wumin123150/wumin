package com.wumin.common.dto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageQuery {
  private Integer pageNo;
  private Integer pageSize;
  private String sortBy;
  private String sortDir;
  private String searchKey;

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDir() {
    return sortDir;
  }

  public void setSortDir(String sortDir) {
    this.sortDir = sortDir;
  }

  public String getSearchKey() {
    return searchKey;
  }

  public void setSearchKey(String searchKey) {
    this.searchKey = searchKey;
  }

  public PageRequest buildPageRequest() {
    return buildPageRequest(1, 10);
  }

  public PageRequest buildPageRequest(int pageNo, int pageSize) {
    return buildPageRequest(pageNo, pageSize, null);
  }

  public PageRequest buildPageRequest(Sort sort) {
    return buildPageRequest(1, 10, sort);
  }

  public PageRequest buildPageRequest(int pageNo, int pageSize, Sort defaultSort) {
    int page = this.pageNo != null ? (this.pageNo - 1) : (pageNo - 1);
    int size = this.pageSize != null ? this.pageSize : pageSize;

    if (StringUtils.isEmpty(this.sortBy)) {
      if(defaultSort == null) {
        return PageRequest.of(page, size);
      } else {
        return PageRequest.of(page, size, defaultSort);
      }
    } else {
      Sort.Direction direction = StringUtils.isNotEmpty(this.sortDir) ? Sort.Direction.fromString(this.sortDir) : Sort.Direction.DESC;
      String[] properties = StringUtils.split(this.sortBy, ",");
      return PageRequest.of(page, size, direction, properties);
    }
  }

  public boolean hasSort() {
    return StringUtils.isNotEmpty(sortBy);
  }

}
