package com.wumin.common.security.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;

public class WeixinToken implements HostAuthenticationToken {

    private String openId;
	private String host;

	public WeixinToken() {
	}

	public WeixinToken(String openId) {
		this(openId, null);
	}

	public WeixinToken(String openId, String host) {
		this.openId = openId;
		this.host = host;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Object getPrincipal() {
		return getOpenId();
	}

	public Object getCredentials() {
		return getOpenId();
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void clear() {
		this.host = null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" - ");
		if (this.host != null) {
			sb.append(" (").append(this.host).append(")");
		}
		return sb.toString();
	}
}
