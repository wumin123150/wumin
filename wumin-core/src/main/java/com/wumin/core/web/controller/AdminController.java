package com.wumin.core.web.controller;

import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 管理员首页
 *
 * @author walker
 */
@Api(description = "管理员引导")
@Controller
public class AdminController {

  @Value("${system.theme:ace}")
  private String theme;

  @RequiresRoles("admin")
  @RequestMapping(value = "/admin/index", method = RequestMethod.GET)
  public String index() {
    return theme + "/index";
  }

  @RequestMapping(value = "/admin/home", method = RequestMethod.GET)
  public String home(Model model) {
    model.addAttribute("usedMemory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    model.addAttribute("freeMemory", Runtime.getRuntime().freeMemory());
    model.addAttribute("maxMemory", Runtime.getRuntime().maxMemory());


    return theme + "/home";
  }

}
