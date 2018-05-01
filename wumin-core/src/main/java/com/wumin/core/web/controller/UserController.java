package com.wumin.core.web.controller;

import com.wumin.common.collection.CollectionExtractor;
import com.wumin.common.collection.ListUtil;
import com.wumin.core.entity.User;
import com.wumin.core.service.RoleService;
import com.wumin.core.service.UserGroupService;
import com.wumin.core.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 管理员管理用户的Controller.
 *
 * @author walker
 */
@Api(description = "用户管理")
@Controller
@RequestMapping(value = "/admin/user")
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private static final String MENU = "user";
  private static final String ACTION_CREATE = "create";
  private static final String ACTION_UPDATE = "update";

  @Value("${system.theme:layui}")
  private String theme;

  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private UserGroupService userGroupService;

  @ApiOperation(value = "用户列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/user/userList";
  }

  @ApiOperation(value = "用户新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String create(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_CREATE);

    model.addAttribute("user", new User());
    model.addAttribute("selectedRoleIds", ListUtil.newArrayList());
    model.addAttribute("roles", roleService.getAll());
    return theme + "/user/userForm";
  }

  @ApiOperation(value = "用户修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String update(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_UPDATE);

    User user = userService.get(id);
    model.addAttribute("user", user);
    model.addAttribute("selectedRoleIds", CollectionExtractor.extractToList(user.getRoleList(), "id"));
    model.addAttribute("roles", roleService.getAll());
    return theme + "/user/userForm";
  }

  @ApiOperation(value = "重置密码页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "reset/{id}", method = RequestMethod.GET)
  public String reset(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);

    model.addAttribute("user", userService.get(id));
    return theme + "/user/resetPassword";
  }

  @ApiOperation(value = "用户树形单选页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "treeSelect", method = RequestMethod.GET)
  public String select(Model model) {
    return theme + "/user/userTreeSelect";
  }

  @ApiOperation(value = "用户树形多选页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "treeCheckbox", method = RequestMethod.GET)
  public String checkbox(Model model) {
    return theme + "/user/userTreeCheckbox";
  }

}
