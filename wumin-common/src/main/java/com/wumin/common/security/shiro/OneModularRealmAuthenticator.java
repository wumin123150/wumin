package com.wumin.common.security.shiro;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.Realm;

public class OneModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        for (Realm realm : realms) {
            if (realm.supports(token)) {
                AuthenticationInfo info = realm.getAuthenticationInfo(token);
                if (info == null) {
                    String msg = "Realm [" + realm
                            + "] was unable to find account data for the submitted AuthenticationToken [" + token
                            + "].";
                    throw new UnknownAccountException(msg);
                }
                return info;
            }
        }
        String msg = "Realms do not support authentication token [" + token + "].  Please ensure that the appropriate Realm implementation "
                + "is configured correctly or that the realm accepts AuthenticationTokens of this type.";
        throw new UnsupportedTokenException(msg);
    }

}
