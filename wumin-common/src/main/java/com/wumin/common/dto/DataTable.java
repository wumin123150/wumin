package com.wumin.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class DataTable<T> {

	private int sEcho;
	private long iTotalRecords;
	private long iTotalDisplayRecords;
	private List<T> entities;

	public DataTable() {
		
	}
	
	public DataTable(int sEcho) {
		this.sEcho = sEcho;
	}
	
	public DataTable(int sEcho, Page<T> page) {
		this.sEcho = sEcho;
		this.iTotalDisplayRecords = page.getTotalElements();
		this.iTotalRecords = page.getTotalElements();
		this.entities = page.getContent();
	}
	
	public DataTable(int sEcho, List<T> entities, long totalElements) {
		this.sEcho = sEcho;
		this.iTotalDisplayRecords = totalElements;
		this.iTotalRecords = totalElements;
		this.entities = entities;
	}
	
	public int getsEcho() {
		return sEcho;
	}

	public void setsEcho(int sEcho) {
		this.sEcho = sEcho;
	}

	public long getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<T> getEntities() {
		return entities;
	}

	public void setEntities(List<T> entities) {
		this.entities = entities;
	}

}
