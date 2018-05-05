package com.wumin.core.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.wumin.common.collection.ArrayUtil;
import com.wumin.common.security.shiro.EhCacheManager;
import com.wumin.common.security.shiro.OneModularRealmAuthenticator;
import com.wumin.core.service.ShiroPasswordRealm;
import com.wumin.core.web.filter.PasswordAuthenticationFilter;
import com.wumin.core.web.filter.ShiroLogoutFilter;
import com.wumin.core.web.filter.ShiroPermissionFilter;
import com.wumin.core.web.filter.ShiroRoleFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

  private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
    Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
    filterChainDefinitionMap.put("/admin/login", "adminAuthc");
    filterChainDefinitionMap.put("/logout", "logout");

    filterChainDefinitionMap.put("/admin/**", "adminAuthc, adminUser, roles[admin]");

    filterChainDefinitionMap.put("/login", "authc");
    filterChainDefinitionMap.put("/static/**", "anon");
    filterChainDefinitionMap.put("/ajaxLogin", "anon");
    filterChainDefinitionMap.put("/api/captcha/**", "anon");
    filterChainDefinitionMap.put("/api/user/checkLoginName", "anon");
    filterChainDefinitionMap.put("/api/retrieve", "anon");
    filterChainDefinitionMap.put("/api/config/get/**", "anon");
    filterChainDefinitionMap.put("/api/config/find/**", "anon");
    filterChainDefinitionMap.put("/api/area/**", "anon");
    filterChainDefinitionMap.put("/api/**", "authc");

    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
  }

  @Bean("adminAuthcFilter")
  public AuthenticatingFilter adminAuthcFilter() {
    AuthenticatingFilter filter = new PasswordAuthenticationFilter();
    filter.setLoginUrl("/admin/login");
    filter.setSuccessUrl("/admin/index");
    return filter;
  }

  @Bean("adminUserFilter")
  public AccessControlFilter adminUserFilter() {
    UserFilter filter = new UserFilter();
    filter.setLoginUrl("/admin/login");
    return filter;
  }

  @Bean(name = "authcFilter")
  public AuthenticatingFilter authcFilter() {
    AuthenticatingFilter filter = new PasswordAuthenticationFilter();
    filter.setLoginUrl("/login");
    filter.setSuccessUrl("/index");
    return filter;
  }

  @Bean("shiroFilter")
  public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(securityManager);

    //配置登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
    shiroFilterFactoryBean.setLoginUrl("/login");
    // 登录成功后要跳转的链接
    shiroFilterFactoryBean.setSuccessUrl("/");
    //未授权界面;
//    shiroFilterFactoryBean.setUnauthorizedUrl("/403");

    Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
    filters.put("adminAuthc", adminAuthcFilter());
    filters.put("adminUser", adminUserFilter());
    filters.put("authc", authcFilter());
    filters.put("logout", new ShiroLogoutFilter());
    filters.put("roles", new ShiroRoleFilter());
    filters.put("perms", new ShiroPermissionFilter());
    shiroFilterFactoryBean.setFilters(filters);

    loadShiroFilterChain(shiroFilterFactoryBean);
    return shiroFilterFactoryBean;
  }

  @Bean
  public AuthorizingRealm shiroRealm() {
    return new ShiroPasswordRealm();
  }

  @Bean(name = "securityManager")
  public SecurityManager securityManager(AuthorizingRealm shiroRealm) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setAuthenticator(new OneModularRealmAuthenticator());
    securityManager.setRealms(ArrayUtil.asList(shiroRealm));
    securityManager.setCacheManager(ehCacheManager());
    securityManager.setSessionManager(sessionManager());
    securityManager.setRememberMeManager(rememberMeManager());
    SecurityUtils.setSecurityManager(securityManager);
    return securityManager;
  }

  @Bean(name = "sessionManager")
  public DefaultWebSessionManager sessionManager() {
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setSessionDAO(sessionDAO());
    sessionManager.setGlobalSessionTimeout(1800000);
    sessionManager.setSessionValidationInterval(1800000);
    sessionManager.setSessionValidationSchedulerEnabled(true);
    sessionManager.setSessionIdCookie(simpleCookie());
    sessionManager.setSessionIdCookieEnabled(true);
    return sessionManager;
  }

  @Bean(name = "sessionDAO")
  public EnterpriseCacheSessionDAO sessionDAO() {
    EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
    sessionDAO.setCacheManager(ehCacheManager());
    return sessionDAO;
  }

  @Bean(name = "simpleCookie")
  public SimpleCookie simpleCookie() {
    SimpleCookie simpleCookie = new SimpleCookie("shiro.sesssion");
    simpleCookie.setPath("/");
    return simpleCookie;
  }

  @Bean(name = "rememberMeManager")
  public CookieRememberMeManager rememberMeManager(){
    CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
    cookieRememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
    cookieRememberMeManager.setCookie(rememberMeCookie());
    return cookieRememberMeManager;
  }

  @Bean(name = "rememberMeCookie")
  public SimpleCookie rememberMeCookie(){
    SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
    simpleCookie.setHttpOnly(true);
    //<!-- 记住我cookie生效时间30天 ,单位秒;-->
    simpleCookie.setMaxAge(259200);
    return simpleCookie;
  }

  @Bean(name = "shiroCacheManager")
  public EhCacheManager ehCacheManager() {
    EhCacheManager ehCacheManager = new EhCacheManager();
    ehCacheManager.setCacheManagerConfigFile("classpath:cache/ehcache-shiro.xml");
    return ehCacheManager;
  }

  @Bean(name = "lifecycleBeanPostProcessor")
  public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }

  @Bean
  public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
    DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
    advisorAutoProxyCreator.setProxyTargetClass(true);
    return advisorAutoProxyCreator;
  }

  /**
   * 开启shiro aop注解支持.
   * 使用代理方式;所以需要开启代码支持;
   */
  @Bean
  public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
    authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
    return authorizationAttributeSourceAdvisor;
  }

  @Bean
  public FilterRegistrationBean delegatingFilterProxy(){
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    DelegatingFilterProxy proxy = new DelegatingFilterProxy();
    proxy.setTargetFilterLifecycle(true);
    proxy.setTargetBeanName("shiroFilter");
    filterRegistrationBean.setFilter(proxy);
    return filterRegistrationBean;
  }

  @Bean
  public ShiroDialect shiroDialect() {
    return new ShiroDialect();
  }

}
