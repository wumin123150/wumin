package com.wumin.common.security.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;

public class CaptchaToken implements HostAuthenticationToken {
	
	public static final String CAPTCHA_SESSION_PHONE = "CAPTCHA_SESSION_PHONE";
	public static final String CAPTCHA_SESSION_KEY = "CAPTCHA_SESSION_KEY";
	public static final String CAPTCHA_SESSION_TIME = "CAPTCHA_SESSION_TIME";
	
	public static final int DEFAULT_TIME = 3;
	
	private String username;
	private String captcha;
	private String host;

	public CaptchaToken() {
	}

	public CaptchaToken(String username, String captcha) {
		this(username, captcha, null);
	}

	public CaptchaToken(String username, String captcha, String host) {
		this.username = username;
		this.captcha = captcha;
		this.host = host;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public Object getPrincipal() {
		return getUsername();
	}

	public Object getCredentials() {
		return getCaptcha();
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void clear() {
		this.captcha = null;
		this.host = null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" - ");
		sb.append(this.captcha);
		if (this.host != null) {
			sb.append(" (").append(this.host).append(")");
		}
		return sb.toString();
	}
}
