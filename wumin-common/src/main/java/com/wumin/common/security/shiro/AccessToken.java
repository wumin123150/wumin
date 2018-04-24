package com.wumin.common.security.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;

public class AccessToken implements HostAuthenticationToken {

    private String token;
	private String host;

	public AccessToken() {
	}

	public AccessToken(String token) {
		this(token, null);
	}

	public AccessToken(String token, String host) {
		this.token = token;
		this.host = host;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Object getPrincipal() {
		return getToken();
	}

	public Object getCredentials() {
		return getToken();
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void clear() {
		this.token = null;
		this.host = null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" - ");
		sb.append(this.token);
		if (this.host != null) {
			sb.append(" (").append(this.host).append(")");
		}
		return sb.toString();
	}
}
