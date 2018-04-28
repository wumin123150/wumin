package com.wumin.core.web.api;

import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.Role;
import com.wumin.core.entity.User;
import com.wumin.core.service.RoleService;
import com.wumin.core.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "角色授权接口")
@RestController
@RequestMapping(value = "/api/userRole")
public class UserRoleEndpoint {

  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;

  @ApiOperation(value = "角色授权分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "roleId", value = "角色ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page")
  public ResponseResult<Page<BaseUser>> page(PageQuery pageQuery, Long roleId, HttpServletRequest request) {
    Page<User> page = userService.getPageByRole(pageQuery.getSearchKey(), roleId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page, User.class, BaseUser.class);
  }

  @ApiOperation(value = "角色授权简单分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "roleId", value = "角色ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page2")
  public ResponseResult<List<BaseUser>> page2(PageQuery pageQuery, Long roleId) {
    Page<User> page = userService.getPageByRole(pageQuery.getSearchKey(), roleId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), User.class, BaseUser.class);
  }

  @ApiOperation(value = "角色授权新增", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "long", paramType = "form"),
    @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "long", paramType = "form")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.POST)
  public ResponseResult create(Long userId, Long roleId) {
    User user = userService.get(userId);
    Role role = roleService.get(roleId);
    if(!user.getRoleList().contains(role)) {
      user.getRoleList().add(role);
      userService.save(user);
      return ResponseResult.SUCCEED;
    } else {
      return ResponseResult.createParamError("用户已存在");
    }
  }

  @ApiOperation(value = "角色授权删除", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "long", paramType = "form"),
    @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "long", paramType = "form")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "delete")
  public ResponseResult delete(Long userId, Long roleId) {
    User user = userService.get(userId);
    for (Role role : user.getRoleList()) {
      if (role.getId().equals(roleId)) {
        user.getRoleList().remove(role);
        break;
      }
    }
    userService.save(user);
    return ResponseResult.SUCCEED;
  }

}
