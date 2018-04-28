package com.wumin.core.web.api;

import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.User;
import com.wumin.core.entity.UserGroup;
import com.wumin.core.service.UserGroupService;
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

@Api(description = "机构授权接口")
@RestController
@RequestMapping(value = "/api/membership")
public class MembershipEndpoint {

  @Autowired
  private UserService userService;
  @Autowired
  private UserGroupService userGroupService;

  @ApiOperation(value = "机构用户列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "groupId", value = "机构ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "list")
  public ResponseResult<List<BaseUser>> list(Long groupId, HttpServletRequest request) {
    List<User> user = userService.getListByGroup(groupId);
    return ResponseResult.createSuccess(user, User.class, BaseUser.class);
  }

  @ApiOperation(value = "机构授权分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "groupId", value = "机构ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page")
  public ResponseResult<Page<BaseUser>> page(PageQuery pageQuery, Long groupId, HttpServletRequest request) {
    Page<User> page = userService.getPageByGroup(pageQuery.getSearchKey(), groupId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page, User.class, BaseUser.class);
  }

  @ApiOperation(value = "机构授权简单分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "groupId", value = "机构ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page2")
  public ResponseResult<List<BaseUser>> page2(PageQuery pageQuery, Long groupId) {
    Page<User> page = userService.getPageByGroup(pageQuery.getSearchKey(), groupId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), User.class, BaseUser.class);
  }

  @ApiOperation(value = "机构授权新增", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "long", paramType = "form"),
    @ApiImplicitParam(name = "groupId", value = "机构ID", required = true, dataType = "long", paramType = "form")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.POST)
  public ResponseResult create(Long userId, Long groupId) {
    User user = userService.get(userId);
    UserGroup group = userGroupService.get(groupId);
    if(!user.getGroupList().contains(group)) {
      user.getGroupList().add(group);
      userService.save(user);
      return ResponseResult.SUCCEED;
    } else {
      return ResponseResult.createParamError("用户已存在");
    }
  }

  @ApiOperation(value = "机构授权删除", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "long", paramType = "form"),
    @ApiImplicitParam(name = "groupId", value = "机构ID", required = true, dataType = "long", paramType = "form")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "delete")
  public ResponseResult delete(Long userId, Long groupId) {
    User user = userService.get(userId);
    for (UserGroup group : user.getGroupList()) {
      if (group.getId().equals(groupId)) {
        user.getGroupList().remove(group);
        break;
      }
    }
    userService.save(user);
    return ResponseResult.SUCCEED;
  }

}
