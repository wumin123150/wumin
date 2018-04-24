package com.wumin.common.persistence;

import java.util.List;

public class JqGridFilter {

	private String groupOp; // 多字段查询时分组类型，主要是AND或者OR
	private List<JqGridRule> rules;

	public String getGroupOp() {
		return groupOp;
	}

	public void setGroupOp(String groupOp) {
		this.groupOp = groupOp;
	}

	public List<JqGridRule> getRules() {
		return rules;
	}

	public void setRules(List<JqGridRule> rules) {
		this.rules = rules;
	}

}
