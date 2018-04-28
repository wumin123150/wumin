package com.wumin.core.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LoginController负责打开登录页面(GET请求)和登录出错页面(POST请求)，
 * <p>
 * 真正登录的POST请求由Filter完成,
 *
 * @author walker
 */
@Api(description = "管理员登录管理")
@Controller
public class LoginController {

  @Value("${system.theme:layui}")
  private String theme;

  @ApiOperation(value = "登录页面", httpMethod = "GET")
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login() {
    return theme + "/login";
  }

  @ApiOperation(value = "登录", httpMethod = "POST")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
    model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
    return theme + "/login";
  }

}
