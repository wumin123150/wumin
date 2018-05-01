package com.wumin.core.service;

import com.google.common.collect.Sets;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.security.shiro.*;
import com.wumin.common.time.DateUtil;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.Token;
import com.wumin.core.entity.User;
import com.wumin.core.entity.UserGroup;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

public class ShiroAccessRealm extends AuthorizingRealm {

  @Autowired
  private UserService userService;
  @Autowired
  private TokenService tokenService;

  /**
   * 认证回调函数,登录时调用.
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
    String id = ((AccessToken) authcToken).getToken();
    if (id == null) {
      throw new AccountException("Null token are not allowed by this realm.");
    }

    Token token = tokenService.get(id);
    if (token == null) {
      throw new AccountException("Null token are not allowed by this realm.");
    }

    User user = token.getUser();
    if (!token.isEnabled()) {
      if (tokenService.activate(token.getUser().getId())) {
        throw new SingleAccountException();
      } else {
        throw new ExpiredCredentialsException();
      }
    } else {
      //自动延长token有效期
      if (token.renew(DateUtil.addDays(new Date(), Token.DEFAULT_RENEW))) {
        token.setExpireTime(DateUtil.addDays(new Date(), Token.DEFAULT_EXPIRE));
        tokenService.save(token);
      }
    }

    if (user != null) {
      if (!user.isEnabled()) {
        if (user.isAccountInactived()) {
          throw new InactiveAccountException();
        }
        if (user.isAccountExpired()) {
          throw new ExpiredCredentialsException();
        }
        if (user.isAccountLocked()) {
          throw new LockedAccountException();
        }
        if (user.isAccountTerminated()) {
          throw new DisabledAccountException();
        }
      }

      return new SimpleAuthenticationInfo(BeanMapper.map(user, ShiroUser.class), null, getName());
    } else {
      throw new UnknownAccountException("No account found");
    }
  }

  /**
   * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
    User user = userService.getByLoginName(shiroUser.getLoginName());

    Set<Role> roles = Sets.newTreeSet(new RoleComparator());
    for (UserGroup group : user.getGroupList()) {
      if (!group.getDelFlag()) {
        roles.addAll(group.getRoleList());
      }
    }
    roles.addAll(user.getRoleList());

    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    for (Role role : roles) {
      // 基于Role的权限信息
      info.addRole(role.getCode());
      // 基于Permission的权限信息
      info.addStringPermissions(role.getPermissions());
    }
    return info;
  }

  /**
   * 设定Password校验的Hash算法与迭代次数.
   */
  @PostConstruct
  public void initCredentialsMatcher() {
    SkipCredentialsMatcher matcher = new SkipCredentialsMatcher();
    setCredentialsMatcher(matcher);
  }

  @PostConstruct
  public void initAuthenticationTokenClass() {
    setAuthenticationTokenClass(AccessToken.class);
  }

  public static class RoleComparator implements Comparator<Role>, Serializable {
    public int compare(Role role1, Role role2) {
      return role1.getCode().compareTo(role2.getCode());
    }
  }
}
