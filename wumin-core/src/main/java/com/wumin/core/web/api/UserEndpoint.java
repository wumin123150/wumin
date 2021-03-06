package com.wumin.core.web.api;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.UserDto;
import com.wumin.core.entity.Annex;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.User;
import com.wumin.core.service.AnnexService;
import com.wumin.core.service.BaseUserService;
import com.wumin.core.service.UserService;
import com.wumin.core.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Api(description = "用户接口")
@RestController
@RequestMapping(value = "/api/user")
public class UserEndpoint {

  @Autowired
  private UserService userService;
  @Autowired
  private BaseUserService baseUserService;
  @Autowired
  private AnnexService annexService;

  @ApiOperation(value = "用户智能搜索", notes = "按账号和名称查询", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "search")
  public ResponseResult<Page<BaseUser>> search(PageQuery pageQuery) {
    Page<BaseUser> users = baseUserService.getPage(new SearchRequest(pageQuery, new Sort(Sort.Direction.DESC, "name"), "name", "loginName"));
    return ResponseResult.createSuccess(users);
  }

  @ApiOperation(value = "用户分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page")
  public ResponseResult<Page<UserVo>> page(PageQuery pageQuery, Boolean advance, HttpServletRequest request) {
    Page<User> page = null;
    if (BooleanUtils.isTrue(advance)) {
      JqGridFilter jqGridFilter = JsonMapper.INSTANCE.fromJson(request.getParameter("filters"), JqGridFilter.class);
      page = userService.getPage(new SearchRequest(jqGridFilter, pageQuery));
    } else {
      page = userService.getPage(new SearchRequest(pageQuery, "name", "loginName", "mobile"));
    }
    return ResponseResult.createSuccess(page, User.class, UserVo.class);
  }

  @ApiOperation(value = "用户简单分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "loginName", value = "账号", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "name", value = "姓名", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "mobile", value = "手机", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "email", value = "邮箱", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "status", value = "状态(I:未激活,A:正常,E:过期,L:锁定,T:终止)", required = false, dataType = "string", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page2")
  public ResponseResult<List<UserVo>> page2(PageQuery pageQuery, String loginName, String name, String mobile, String email, String status) {
    SearchRequest searchRequest = new SearchRequest(pageQuery, "name", "loginName", "mobile")
      .addContain("loginName", loginName)
      .addContain("name", name)
      .addContain("mobile", mobile)
      .addContain("email", email)
      .addEqualToNotEmpty("status", status);
    Page<User> page = userService.getPage(searchRequest);
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), User.class, UserVo.class);
  }

  @ApiOperation(value = "用户获取", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "get/{id}")
  public ResponseResult<UserVo> get(@PathVariable("id") Long id) {
    User user = userService.get(id);
    return ResponseResult.createSuccess(user, UserVo.class);
  }

  @ApiOperation(value = "用户新增", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "create", method = RequestMethod.POST)
  public ResponseResult create(UserDto userDto) throws IOException {
    ComplexResult result = validate(userDto);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    User user = BeanMapper.map(userDto, User.class);
    user.setPlainPassword(userDto.getPassword());
    user = userService.save(user);

    if(StringUtils.isNotEmpty(userDto.getAvatarId())) {
      Annex annex = annexService.move(userDto.getAvatarId(), String.valueOf(user.getId()), User.USER_ANNEX_TYPE, User.USER_ANNEX_PATH);
      user.setAvatar(annex.getPath());
      userService.save(user);
    }

    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "用户修改", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "update", method = RequestMethod.POST)
  public ResponseResult update(UserDto userDto) throws IOException {
    ComplexResult result = validate(userDto);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    User user = userService.get(userDto.getId());
    userDto.setLoginName(user.getLoginName());//修改不能设置账号
    userDto.setPassword(null);//修改不能设置密码
    user.setPlainPassword(null);
    user = BeanMapper.map(userDto, user, UserDto.class, User.class);

    if(StringUtils.isNotEmpty(userDto.getAvatarId())) {
      Annex annex = annexService.move(userDto.getAvatarId(), String.valueOf(user.getId()), User.USER_ANNEX_TYPE, User.USER_ANNEX_PATH);
      user.setAvatar(annex.getPath());
    }

    userService.save(user);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "密码重置", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "reset", method = RequestMethod.POST)
  public ResponseResult reset(Long userId, String password) {
    if (StringUtils.isEmpty(password)) {
      return ResponseResult.createParamError("密码不能为空");
    }

    userService.changePassword(userId, password);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "用户停用", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") Long id) {
    userService.delete(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "用户启用", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "start/{id}")
  public ResponseResult start(@PathVariable("id") Long id) {
    userService.start(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "检查账号是否存在", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "loginName", value = "账号", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping(value = "checkLoginName")
  public boolean checkLoginName(Long id, String loginName) {
    return !userService.existLoginName(id, loginName);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

  private ComplexResult validate(UserDto user) {
    ComplexResult result = FluentValidator.checkAll()
      .on(user, new HibernateSupportedValidator<UserDto>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator()))
      .on(user, new ValidatorHandler<UserDto>() {
        public boolean validate(ValidatorContext context, UserDto t) {
          if(StringUtils.containsWhitespace(t.getLoginName())) {
            context.addErrorMsg("账号不能有空格");
            return false;
          }
          if (userService.existLoginName(t.getId(), t.getLoginName())) {
            context.addErrorMsg("账号已经被使用");
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
