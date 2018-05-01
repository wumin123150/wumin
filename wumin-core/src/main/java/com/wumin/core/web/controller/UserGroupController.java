package com.wumin.core.web.controller;

import com.wumin.common.collection.CollectionExtractor;
import com.wumin.common.collection.ListUtil;
import com.wumin.core.entity.UserGroup;
import com.wumin.core.service.RoleService;
import com.wumin.core.service.UserGroupService;
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

@Api(description = "机构管理")
@Controller
@RequestMapping(value = "/admin/userGroup")
public class UserGroupController {

  private static final String MENU = "userGroup";

  @Value("${system.theme:layui}")
  private String theme;

  @Autowired
  private UserGroupService userGroupService;
  @Autowired
  private RoleService roleService;

  @ApiOperation(value = "机构列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("roles", roleService.getAll());
    return theme + "/userGroup/userGroupList";
  }

  @ApiOperation(value = "机构新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create")
  public String create(Long parentId, Model model) {
    UserGroup userGroup = new UserGroup();
    model.addAttribute("userGroup", userGroup);
    model.addAttribute("parent", parentId == null ? null : userGroupService.get(parentId));
    model.addAttribute("selectedRoleIds", ListUtil.newArrayList());
    model.addAttribute("roles", roleService.getAll());
    return theme + "/userGroup/userGroupForm";
  }

  @ApiOperation(value = "机构修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String update(@PathVariable("id") Long id, Model model) {
    UserGroup userGroup = userGroupService.get(id);
    model.addAttribute("userGroup", userGroup);
    model.addAttribute("parent", userGroup.getParentId() == null ? null : userGroupService.get(userGroup.getParentId()));
    model.addAttribute("selectedRoleIds", CollectionExtractor.extractToList(userGroup.getRoleList(), "id"));
    model.addAttribute("roles", roleService.getAll());
    return theme + "/userGroup/userGroupForm";
  }

  @ApiOperation(value = "机构授权页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "auth/{id}")
  public String authorize(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("groupId", id);
    return theme + "/userGroup/userGroupAuth";
  }

  @ApiOperation(value = "机构单选页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "select", method = RequestMethod.GET)
  public String select(Model model) {
    return theme + "/userGroup/userGroupSelect";
  }

  @ApiOperation(value = "机构多选页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "checkbox", method = RequestMethod.GET)
  public String checkbox(Model model) {
    return theme + "/userGroup/userGroupCheckbox";
  }

}
