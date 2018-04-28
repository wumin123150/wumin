package com.wumin.core.web.filter;

import com.wumin.common.base.ObjectUtil;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.net.IPUtil;
import com.wumin.common.net.RequestUtil;
import com.wumin.common.security.shiro.CaptchaException;
import com.wumin.common.security.shiro.InactiveAccountException;
import com.wumin.common.security.shiro.ShiroUser;
import com.wumin.common.web.Servlets;
import com.wumin.core.entity.Log;
import com.wumin.core.service.UserService;
import com.wumin.core.web.LogQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 自定义登录认证filter
 */

public class PasswordAuthenticationFilter extends FormAuthenticationFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordAuthenticationFilter.class);

  @Autowired
  private UserService userService;

  // 开始时间
  private long startTime = 0L;
  // 结束时间
  private long endTime = 0L;

  public boolean continueLogin(ServletRequest request, ServletResponse response) throws Exception {
    return false;
  }

  public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
    startTime = System.currentTimeMillis();

    boolean isAllowed = isAccessAllowed(request, response, mappedValue);

    if (!isAllowed && !isLoginRequest(request, response)) {
      isAllowed = continueLogin(request, response);
    }

    // 登录跳转
    if (isAllowed && isLoginRequest(request, response)) {
      issueSuccessRedirect(request, response);
      return false;
    }

    SavedRequest lastRequest = WebUtils.getSavedRequest(request);

    boolean result = isAllowed || onAccessDenied(request, response, mappedValue);

    //解决登录转注册后，无法返回原链接
    SavedRequest savedRequest = WebUtils.getSavedRequest(request);
    if (savedRequest != null && StringUtils.equals(getLoginUrl(), savedRequest.getRequestURI())) {
      Subject subject = SecurityUtils.getSubject();
      Session session = subject.getSession();
      session.setAttribute(WebUtils.SAVED_REQUEST_KEY, lastRequest);
    }

    return result;
  }

  public void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
    if (RequestUtil.isAjaxRequest((HttpServletRequest) request)) {
      renderJson(response, ResponseResult.createError(ErrorCodeFactory.UNAUTHORIZED_CODE, "请重新登录。"));
    } else {
      String loginUrl = getLoginUrl();
      WebUtils.issueRedirect(request, response, loginUrl);
    }
  }

  /**
   * 登录成功
   */
  public boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                ServletResponse response) throws Exception {
    ShiroUser user = (ShiroUser) subject.getPrincipal();
    userService.loginSuccess(user.getLoginName(), IPUtil.getIp(WebUtils.toHttp(request)));

    log((HttpServletRequest) request, getUsername(request), "success");

    if (RequestUtil.isAjaxRequest((HttpServletRequest) request)) {
      renderSuccess(subject, request, response);
      return false;
    } else {
      return super.onLoginSuccess(token, subject, request, response);
    }
  }

  public void renderSuccess(Subject subject, ServletRequest request, ServletResponse response, String... headers) {
    renderJson(response, ResponseResult.SUCCEED, headers);
  }

  public boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
                                ServletResponse response) {
    setFailureAttribute(request, e);
    if (RequestUtil.isAjaxRequest((HttpServletRequest) request)) {
      String message = (String) request.getAttribute(getFailureKeyAttribute());
      renderJson(response, ResponseResult.createError(ErrorCodeFactory.UNAUTHORIZED_CODE, message));

      return false;
    } else {
      return true;
    }
  }

  protected void renderJson(final ServletResponse response, final ResponseResult json, final String... headers) {
    Servlets.renderJson((HttpServletResponse) response, JsonMapper.nonEmptyMapper().toJson(json), headers);
  }

  protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
    if (ae instanceof UnknownAccountException) {
      request.setAttribute(getFailureKeyAttribute(), "用户不存在.");
    } else if (ae instanceof IncorrectCredentialsException) {
      request.setAttribute(getFailureKeyAttribute(), "用户或密码错误.");
    } else if (ae instanceof CaptchaException) {
      request.setAttribute(getFailureKeyAttribute(), "动态密码错误.");
    } else if (ae instanceof InactiveAccountException) {
      request.setAttribute(getFailureKeyAttribute(), "用户未激活.");
    } else if (ae instanceof ExpiredCredentialsException) {
      request.setAttribute(getFailureKeyAttribute(), "用户已过期.");
    } else if (ae instanceof LockedAccountException) {
      request.setAttribute(getFailureKeyAttribute(), "用户已锁定.");
    } else if (ae instanceof DisabledAccountException) {
      request.setAttribute(getFailureKeyAttribute(), "用户已停用.");
    } else if (ae instanceof ExcessiveAttemptsException) {
      request.setAttribute(getFailureKeyAttribute(), "用户登录次数过多.");
    } else if (ae instanceof AccountException) {
      request.setAttribute(getFailureKeyAttribute(), "用户名不能为空.");
    } else {
      request.setAttribute(getFailureKeyAttribute(), "登录认证错误，请重试.");
    }
    log((HttpServletRequest) request, getUsername(request), "fail");
  }

  private void log(HttpServletRequest request, String username, String result) {
    Log log = new Log();
    log.setDescription("登录");
    endTime = System.currentTimeMillis();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("登录耗时：{}", endTime - startTime);
    }

    log.setIp(IPUtil.getIp(request));
    log.setUrl(request.getRequestURL().toString());
    log.setMethod(request.getMethod());
    log.setUserAgent(request.getHeader("User-Agent"));
    log.setParameter("{\"username\":\"" + username + "\"}");
    log.setResult(result);
    log.setLogTime(new Date(startTime));
    log.setSpendTime((int) (endTime - startTime));
    log.setUsername(username);
    LogQueue.getInstance().push(log);
  }

}
