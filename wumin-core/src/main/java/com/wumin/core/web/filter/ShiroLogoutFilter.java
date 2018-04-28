package com.wumin.core.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wumin.common.security.shiro.ShiroUser;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.net.RequestUtil;
import com.wumin.common.web.Servlets;
import com.wumin.core.service.TokenService;

public class ShiroLogoutFilter extends LogoutFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroLogoutFilter.class);

  @Autowired
  private TokenService tokenService;

  private JsonMapper jsonMapper = new JsonMapper();

  protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
    Subject subject = getSubject(request, response);
    ShiroUser user = (ShiroUser) subject.getPrincipal();
    String redirectUrl = getRedirectUrl(request, response, subject);
    try {
      subject.logout();
    } catch (SessionException ise) {
      LOGGER.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
    }
    if (RequestUtil.isAjaxRequest((HttpServletRequest) request)) {
      if (user != null) {
        tokenService.disable(user.getId());
      }
      renderJson(response, ResponseResult.SUCCEED);
    } else {
      issueRedirect(request, response, redirectUrl);
    }

    return false;
  }

  private void renderJson(final ServletResponse response, final ResponseResult json, final String... headers) {
    Servlets.renderJson((HttpServletResponse) response, jsonMapper.toJson(json), headers);
  }

}
