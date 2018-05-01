package com.wumin.core.web.api;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.core.dto.UserDto;
import com.wumin.core.entity.Annex;
import com.wumin.core.entity.User;
import com.wumin.core.service.AnnexService;
import com.wumin.core.service.UserService;
import com.wumin.core.web.CoreThreadContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api(description = "账户接口")
@RestController
@RequestMapping(value = "/api/account")
public class AccountEndpoint {

  @Autowired
  private UserService userService;
  @Autowired
  private AnnexService annexService;

  @ApiOperation(value = "账户获取", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "get")
  public ResponseResult<UserDto> get() {
    Long id = CoreThreadContext.getUserId();
    User user = userService.get(id);
    return ResponseResult.createSuccess(user, UserDto.class);
  }

  @ApiOperation(value = "保存头像", notes = "图片转成base64编码", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "avatar", value = "base64编码的图片", required = true, dataType = "string", paramType = "form")
  })
  @RequestMapping(value = "saveAvatar", method = RequestMethod.POST)
  public ResponseResult saveAvatar(String avatar) throws IOException {
    Long id = CoreThreadContext.getUserId();
    byte[] data = Base64.decodeBase64(avatar.getBytes("UTF-8"));
    Annex annex = annexService.save(data, "avatar.jpg", User.USER_ANNEX_PATH, String.valueOf(id), User.USER_ANNEX_TYPE);

    User user = userService.get(id);
    user.setAvatar(annex.getPath());
    userService.save(user);
    return ResponseResult.createSuccess(annex.getPath());
  }

  @ApiOperation(value = "设置头像", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "avatarId", value = "图片ID", required = true, dataType = "string", paramType = "form")
  })
  @RequestMapping(value = "setAvatar", method = RequestMethod.POST)
  public ResponseResult setAvatar(String avatarId) throws IOException {
    Long id = CoreThreadContext.getUserId();
    if(StringUtils.isNotEmpty(avatarId)) {
      User user = userService.get(id);
      Annex annex = annexService.move(avatarId, String.valueOf(user.getId()), User.USER_ANNEX_TYPE, User.USER_ANNEX_PATH);
      user.setAvatar(annex.getPath());
      userService.save(user);
    }

    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "密码修改", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "password", value = "原密码", required = true, dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "string", paramType = "form")
  })
  @LogIgnore(value = "field", excludes = {"password", "newPassword"})
  @RequestMapping(value = "/changePwd", method = RequestMethod.POST)
  public ResponseResult changePassword(String password, String newPassword) {
    Long id = CoreThreadContext.getUserId();
    if (userService.checkPassword(id, password)) {
      userService.changePassword(id, newPassword);
      return ResponseResult.SUCCEED;
    } else {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "原密码错误");
    }
  }

  @ApiOperation(value = "修改用户信息", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "sex", value = "性别", dataType = "int", paramType = "form"),
    @ApiImplicitParam(name = "birthday", value = "出生日期", dataType = "date", paramType = "form"),
    @ApiImplicitParam(name = "mobile", value = "手机", dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "email", value = "邮箱", dataType = "string", paramType = "form")
  })
  @RequestMapping(value = "change", method = RequestMethod.POST)
  public ResponseResult change(String name, Integer sex, Date birthday, String mobile, String email) {
    Long id = CoreThreadContext.getUserId();
    User user = userService.get(id);
    user.setName(name);
    user.setSex(sex);
    user.setBirthday(birthday);
    user.setMobile(mobile);
    user.setEmail(email);
    userService.save(user);
    return ResponseResult.SUCCEED;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

}
