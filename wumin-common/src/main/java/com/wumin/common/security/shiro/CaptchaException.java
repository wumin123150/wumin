package com.wumin.common.security.shiro;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 验证码错误异常
 * 
 * @author walker
 */
public class CaptchaException extends AuthenticationException {

	private static final long serialVersionUID = 1657981799237091295L;

	public CaptchaException() {
		super();
	}

	public CaptchaException(String message, Throwable cause) {
		super(message, cause);
	}

	public CaptchaException(String message) {
		super(message);
	}

	public CaptchaException(Throwable cause) {
		super(cause);
	}

}
