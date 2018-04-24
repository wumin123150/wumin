package com.wumin.common.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

public class Pagination<T> {

  private int page;
  private int size;
  private Sort sort;
  private long total;
  private List<T> content = new ArrayList<T>();

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

}
