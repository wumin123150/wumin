package com.wumin.core.web.controller;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.core.entity.Annex;
import com.wumin.core.entity.User;
import com.wumin.core.service.AnnexService;
import com.wumin.core.service.MessageService;
import com.wumin.core.service.UserService;
import com.wumin.core.web.CoreThreadContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Api(description = "账户管理")
@Controller
public class AccountController {

  @Autowired
  private UserService userService;
  @Autowired
  private AnnexService annexService;
  @Autowired
  private MessageService messageService;

  @Value("${system.theme:layui}")
  private String theme;

  @ApiOperation(value = "修改账户页面", httpMethod = "GET")
  @RequestMapping(value = "/{action}/changeAccount", method = RequestMethod.GET)
  public String changeAccountForm(@PathVariable("action") String action, Model model) {
    model.addAttribute("action", action);
    model.addAttribute("user", userService.get(CoreThreadContext.getUserId()));
    return theme + "/account/changeAccount";
  }

  @ApiOperation(value = "我的消息页面", httpMethod = "GET")
  @RequestMapping(value = "/{action}/readMessage", method = RequestMethod.GET)
  public String messageForm(@PathVariable("action") String action, Model model) {
    model.addAttribute("action", action);
    return theme + "/account/readMessage";
  }

  @ApiOperation(value = "我的消息页面", httpMethod = "GET")
  @RequestMapping(value = "/{action}/message/read/{id}")
  public String message(@PathVariable("action") String action, @PathVariable("id") String id, Model model) {
    messageService.read(id);
    return "redirect:/"+action+"/readMessage";
  }

}
