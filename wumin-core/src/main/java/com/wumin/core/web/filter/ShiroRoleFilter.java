package com.wumin.core.web.filter;

import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.net.RequestUtil;
import com.wumin.common.web.Servlets;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShiroRoleFilter extends RolesAuthorizationFilter {

  public boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
    throws Exception {
    if (RequestUtil.isAjaxRequest((HttpServletRequest) request)) {
      Subject subject = getSubject(request, response);
      if (subject.getPrincipal() == null) {
        renderJson(response, ResponseResult.createError(ErrorCodeFactory.UNAUTHORIZED_CODE, "请重新登录。"));
      } else {
        renderJson(response, ResponseResult.createError(ErrorCodeFactory.FORBIDDEN_CODE, "没有权限访问。"));
      }
      return false;
    }
    return onAccessDenied(request, response);
  }

  private void renderJson(final ServletResponse response, final ResponseResult json, final String... headers) {
    Servlets.renderJson((HttpServletResponse) response, JsonMapper.INSTANCE.toJson(json), headers);
  }

}
