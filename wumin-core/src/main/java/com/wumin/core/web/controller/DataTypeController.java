package com.wumin.core.web.controller;

import com.wumin.core.entity.DataType;
import com.wumin.core.service.DataTypeService;
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

@Api(description = "数据类型管理")
@Controller
@RequestMapping(value = "/admin/dataType")
public class DataTypeController {

  private static final String MENU = "dataDictionary";
  private static final String ACTION_CREATE = "create";
  private static final String ACTION_UPDATE = "update";

  @Value("${system.theme:layui}")
  private String theme;

  @Autowired
  private DataTypeService dataTypeService;

  @ApiOperation(value = "数据类型列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/dataDictionary/dataTypeList";
  }

  @ApiOperation(value = "数据类型新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String create(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_CREATE);

    model.addAttribute("dataType", new DataType());
    return theme + "/dataDictionary/dataTypeForm";
  }

  @ApiOperation(value = "数据类型修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String update(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_UPDATE);

    model.addAttribute("dataType", dataTypeService.get(id));
    return theme + "/dataDictionary/dataTypeForm";
  }

}
