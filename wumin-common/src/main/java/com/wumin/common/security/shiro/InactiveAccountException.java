package com.wumin.common.security.shiro;

import org.apache.shiro.authc.DisabledAccountException;

public class InactiveAccountException extends DisabledAccountException {

    private static final long serialVersionUID = -3301490116189825424L;

    public InactiveAccountException() {
	}

	public InactiveAccountException(String message) {
		super(message);
	}

	public InactiveAccountException(Throwable cause) {
		super(cause);
	}

	public InactiveAccountException(String message, Throwable cause) {
		super(message, cause);
	}
}
