package com.wumin.core.web.controller;

import com.wumin.core.entity.DataDictionary;
import com.wumin.core.service.DataDictionaryService;
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

@Api(description = "数据字典管理")
@Controller
@RequestMapping(value = "/admin/dataDictionary")
public class DataDictionaryController {

  private static final String MENU = "dataDictionary";

  @Value("${system.theme:ace}")
  private String theme;

  @Autowired
  private DataTypeService dataTypeService;
  @Autowired
  private DataDictionaryService dataDictionaryService;

  @ApiOperation(value = "数据字典页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Long typeId, Model model) {
    model.addAttribute("menu", MENU);

    model.addAttribute("type", dataTypeService.get(typeId));
    return theme + "/dataDictionary/dataDictionaryList";
  }

  @ApiOperation(value = "数据字典新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create")
  public String create(Long typeId, Long parentId, Model model) {
    DataDictionary dataDictionary = new DataDictionary();
    dataDictionary.setTypeId(typeId);
    model.addAttribute("dataDictionary", dataDictionary);
    model.addAttribute("parent", parentId == null ? null : dataDictionaryService.get(parentId));
    return theme + "/dataDictionary/dataDictionaryForm";
  }

  @ApiOperation(value = "数据字典修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String update(@PathVariable("id") Long id, Model model) {
    DataDictionary dataDictionary = dataDictionaryService.get(id);
    model.addAttribute("dataDictionary", dataDictionary);
    model.addAttribute("parent", dataDictionary.getParentId() == null ? null : dataDictionaryService.get(dataDictionary.getParentId()));
    return theme + "/dataDictionary/dataDictionaryForm";
  }

}
