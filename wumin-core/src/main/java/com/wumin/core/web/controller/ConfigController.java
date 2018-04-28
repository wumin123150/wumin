package com.wumin.core.web.controller;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.collection.MapUtil;
import com.wumin.common.collection.type.Pair;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.core.entity.Config;
import com.wumin.core.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Api(description = "系统配置管理")
@Controller
@RequestMapping(value = "/admin/config")
public class ConfigController {

  private static final String MENU = "config";
  private static final String ACTION_CREATE = "create";
  private static final String ACTION_UPDATE = "update";

  @Value("${system.theme:layui}")
  private String theme;

  @Autowired
  private ConfigService configService;

  @ApiOperation(value = "系统配置列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/config/configList";
  }

  @ApiOperation(value = "系统配置新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String create(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_CREATE);

    model.addAttribute("config", new Config());
    return theme + "/config/configForm";
  }

  @ApiOperation(value = "系统配置修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String update(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_UPDATE);

    model.addAttribute("config", configService.get(id));
    return theme + "/config/configForm";
  }

  @ApiOperation(value = "系统配置查看页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "view", method = RequestMethod.GET)
  public String view(Model model) {
    model.addAttribute("menu", "configs");

    List<Config> configs = configService.getAll();
    List<Map<String, String>> options = ListUtil.newArrayList();
    for(Config config : configs) {
      if(StringUtils.isNotEmpty(config.getParams())) {
        options.add(JsonMapper.INSTANCE.fromJson(config.getParams(), Map.class));
      } else {
        options.add(null);
      }
    }
    model.addAttribute("configs", configs);
    model.addAttribute("options", options);
    return theme + "/config/configView";
  }

}
