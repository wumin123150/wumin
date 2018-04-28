package com.wumin.core.web.controller;

import com.wumin.core.entity.MessageText;
import com.wumin.core.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(description = "消息管理")
@Controller
@RequestMapping(value = "/admin/message")
public class MessageController {

  private static final String MENU = "message";
  private static final String ACTION_CREATE = "create";
  private static final String ACTION_UPDATE = "update";

  @Value("${system.theme:ace}")
  private String theme;

  @Autowired
  private MessageService messageService;

  @ApiOperation(value = "消息列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/message/messageList";
  }

  @ApiOperation(value = "消息新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String create(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_CREATE);

    model.addAttribute("text", new MessageText());
    return theme + "/message/messageForm";
  }

}
