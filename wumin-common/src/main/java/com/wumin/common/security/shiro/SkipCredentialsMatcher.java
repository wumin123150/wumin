package com.wumin.common.security.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

public class SkipCredentialsMatcher extends SimpleCredentialsMatcher {

	public SkipCredentialsMatcher() {
		super();
	}

	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		return true;
	}

}
