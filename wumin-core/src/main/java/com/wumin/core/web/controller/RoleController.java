package com.wumin.core.web.controller;

import com.wumin.core.entity.Permission;
import com.wumin.core.entity.Role;
import com.wumin.core.service.PermissionService;
import com.wumin.core.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Api(description = "角色管理")
@Controller
@RequestMapping(value = "/admin/role")
public class RoleController {

  private static final String MENU = "role";
  private static final String ACTION_CREATE = "create";
  private static final String ACTION_UPDATE = "update";

  @Value("${system.theme:layui}")
  private String theme;

  @Autowired
  private RoleService roleService;
  @Autowired
  private PermissionService permissionService;

  @ApiOperation(value = "角色列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/role/roleList";
  }

  @ApiOperation(value = "角色新增页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String createForm(Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_CREATE);

    model.addAttribute("role", new Role());
    model.addAttribute("permissions", permissionService.getAll());
    return theme + "/role/roleForm";
  }

  @ApiOperation(value = "角色新增", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.POST)
  public String create(@Valid Role role, BindingResult result,
                       @RequestParam(value = "permissions", required = false) List<Long> checkedPermissions,
                       Model model, RedirectAttributes redirectAttributes) {
    if (checkedPermissions != null) {
      for (Long permissionId : checkedPermissions) {
        Permission permission = new Permission(permissionId);
        role.getPermissionList().add(permission);
      }
    }

    if (roleService.existCode(role.getId(), role.getCode())) {
      result.addError(new FieldError("role", "code", "编码已经被使用"));
    }

    if (result.hasErrors()) {
      model.addAttribute("menu", MENU);
      model.addAttribute("action", ACTION_CREATE);

      model.addAttribute("role", role);
      model.addAttribute("permissions", permissionService.getAll());

      model.addAttribute("errors", result);
      return theme + "/role/roleForm";
    } else
      roleService.save(role);

    redirectAttributes.addFlashAttribute("message", "新增角色成功");
    return "redirect:/admin/role";
  }

  @ApiOperation(value = "角色修改页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "update/{id}", method = RequestMethod.GET)
  public String updateForm(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("action", ACTION_UPDATE);

    model.addAttribute("role", roleService.get(id));
    model.addAttribute("permissions", permissionService.getAll());
    return theme + "/role/roleForm";
  }

  /**
   * 演示自行绑定表单中的checkBox permissionList到对象中.
   */
  @ApiOperation(value = "角色修改", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "update", method = RequestMethod.POST)
  public String update(@Valid @ModelAttribute("preloadRole") Role role, BindingResult result,
                       @RequestParam(value = "permissions", required = false) List<Long> checkedPermissions,
                       Model model, RedirectAttributes redirectAttributes) {
    // bind permissions
    role.getPermissionList().clear();
    if (checkedPermissions != null) {
      for (Long permissionId : checkedPermissions) {
        Permission permission = new Permission(permissionId);
        role.getPermissionList().add(permission);
      }
    }

    if (roleService.existCode(role.getId(), role.getCode())) {
      result.addError(new FieldError("role", "code", "编码已经被使用"));
    }

    if (result.hasErrors()) {
      model.addAttribute("menu", MENU);
      model.addAttribute("action", ACTION_UPDATE);

      model.addAttribute("role", role);
      model.addAttribute("permissions", permissionService.getAll());

      model.addAttribute("errors", result);
      return theme + "/role/roleForm";
    } else
      roleService.save(role);

    redirectAttributes.addFlashAttribute("message", "保存角色成功");
    return "redirect:/admin/role";
  }

  @ApiOperation(value = "角色授权页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "auth/{id}")
  public String authorize(@PathVariable("id") Long id, Model model) {
    model.addAttribute("menu", MENU);
    model.addAttribute("roleId", id);
    model.addAttribute("roles", roleService.getAll());
    return theme + "/role/roleAuth";
  }

  /**
   * 使用@ModelAttribute, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出对象,再把Form提交的内容绑定到该对象上。
   * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
   */
  @ModelAttribute("preloadRole")
  public Role getRole(@RequestParam(value = "id", required = false) Long id) {
    if (id != null) {
      return roleService.get(id);
    }
    return null;
  }

}
