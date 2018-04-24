package com.wumin.common.security.shiro;

import org.apache.shiro.authc.AccountException;

/**
 * 单个错误异常
 * 
 * @author walker
 */
public class SingleAccountException extends AccountException {

	private static final long serialVersionUID = 1552643658692395780L;

	public SingleAccountException() {
		super();
	}

	public SingleAccountException(String message) {
		super(message);
	}

}
