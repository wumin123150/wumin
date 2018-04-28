package com.wumin.core.web.controller;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(description = "日志管理")
@Controller
@RequestMapping(value = "/admin/log")
public class LogController {

  private static final String MENU = "log";

  @Value("${system.theme:ace}")
  private String theme;

  @Autowired
  private LogService logService;

  @ApiOperation(value = "日志列表页面", httpMethod = "GET")
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/log/logList";
  }

  @ApiOperation(value = "日志查看页面", httpMethod = "GET")
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
  public String view(@PathVariable("id") String id, Model model) {
    model.addAttribute("log", logService.get(id));
    return theme + "/log/logView";
  }

}
