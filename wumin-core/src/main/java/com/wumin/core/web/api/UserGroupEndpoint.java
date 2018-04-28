package com.wumin.core.web.api;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.core.dto.UserGroupDto;
import com.wumin.core.entity.UserGroup;
import com.wumin.core.service.UserGroupService;
import com.wumin.core.service.UserService;
import com.wumin.core.vo.UserGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import java.util.List;

@Api(description = "机构接口")
@RestController
@RequestMapping(value = "/api/userGroup")
public class UserGroupEndpoint {

  @Autowired
  private UserService userService;
  @Autowired
  private UserGroupService userGroupService;

  @ApiOperation(value = "根据父节点ID获取机构列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "tree")
  public ResponseResult<List<UserGroupDto>> tree(Long id) {
    List<UserGroup> userGroups = null;
    if (id != null) {
      if(id == 0) {
        userGroups = userGroupService.findTop();
      } else {
        userGroups = userGroupService.findChildrenByParent(id);
      }
    } else {
      userGroups = userGroupService.getAll();
    }
    return ResponseResult.createSuccess(userGroups, UserGroup.class, UserGroupDto.class);
  }

  @ApiOperation(value = "机构列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "list")
  public ResponseResult<List<UserGroupDto>> list() {
    List<UserGroup> userGroups = userGroupService.getAll();
    return ResponseResult.createSuccess(userGroups, UserGroup.class, UserGroupDto.class);
  }

  @ApiOperation(value = "机构分页列表", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "page")
  public ResponseResult<Page<UserGroupDto>> page(PageQuery pageQuery, Long parentId, HttpServletRequest request) {
    Page<UserGroup> page = userGroupService.getPage(pageQuery.getSearchKey(), parentId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page, UserGroup.class, UserGroupDto.class);
  }

  @ApiOperation(value = "机构简单分页列表", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "page2")
  public ResponseResult<List<UserGroupDto>> page2(PageQuery pageQuery, Long parentId) {
    Page<UserGroup> page = userGroupService.getPage(pageQuery.getSearchKey(), parentId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), UserGroup.class, UserGroupDto.class);
  }

  @ApiOperation(value = "机构保存", httpMethod = "POST", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public ResponseResult save(UserGroupVo userGroupVo) {
    ComplexResult result = validate(userGroupVo);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    UserGroup userGroup = null;
    if(userGroupVo.getId() == null) {
      userGroup = BeanMapper.map(userGroupVo, UserGroup.class);

      UserGroup parent = null;
      if (userGroup.getParentId() != null) {
        parent = userGroupService.get(userGroup.getParentId());
        if (parent != null) {
          userGroup.setGrade(parent.getGrade() + 1);
        }
      } else {
        userGroup.setGrade(1);
      }

      if (parent != null) {
        userGroup.setPath(parent.getPath() + UserGroup.PATH_SPLIT + userGroup.getCode());
      } else {
        userGroup.setPath(UserGroup.PATH_SPLIT + userGroup.getCode());
      }
    } else {
      userGroup = userGroupService.get(userGroupVo.getId());
      BeanMapper.map(userGroupVo, userGroup, UserGroupVo.class, UserGroup.class);
    }

    userGroupService.save(userGroup);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "机构删除", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") Long id) {
    if (userService.existGroup(id)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "请先删除此机构下的用户");
    }

    List<UserGroup> children = userGroupService.findChildrenByParent(id);
    if(!ListUtil.isEmpty(children)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "请先删除下级机构");
    }

    userGroupService.delete(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "机构获取", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "get/{id}")
  public ResponseResult get(@PathVariable("id") Long id) {
    UserGroup userGroup = userGroupService.get(id);
    return ResponseResult.createSuccess(userGroup, UserGroupDto.class);
  }

  /**
   * 判断代码的唯一性
   */
  @ApiOperation(value = "检查机构代码是否存在", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "代码", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping("checkCode")
  public boolean checkCode(Long id, String code) {
    return !userGroupService.existCode(id, code);
  }

  private ComplexResult validate(UserGroupVo userGroup) {
    ComplexResult result = FluentValidator.checkAll()
      .on(userGroup, new HibernateSupportedValidator<UserGroupVo>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator()))
      .on(userGroup, new ValidatorHandler<UserGroupVo>() {
        public boolean validate(ValidatorContext context, UserGroupVo t) {
          if (userGroupService.existCode(t.getId(), t.getCode())) {
            context.addErrorMsg("代码已经被使用");
            return false;
          }
          return true;
        }
      })
      .doValidate()
      .result(ResultCollectors.toComplex());
    return result;
  }

}
