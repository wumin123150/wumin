package com.wumin.core.web;

import com.wumin.common.net.IPUtil;
import com.wumin.common.net.RequestUtil;
import com.wumin.common.security.shiro.ShiroUser;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.service.BaseUserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ContextInterceptor extends HandlerInterceptorAdapter {

  @Value("${upload.folder:/opt/upload/}")
  private String uploadPath;

  @Autowired
  private BaseUserService baseUserService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
    if (user != null) {
      BaseUser baseUser = baseUserService.get(user.getId());
      CoreThreadContext.setUser(baseUser);
    }

    CoreThreadContext.setIp(IPUtil.getIp(request));
    CoreThreadContext.setUrl(RequestUtil.getLocation(request));
    CoreThreadContext.setUploadPath(uploadPath);

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    if (modelAndView != null) {

    }
  }

  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    throws Exception {
    CoreThreadContext.reset();

    if (ex != null) {
      ex.printStackTrace();
    }
  }

}
