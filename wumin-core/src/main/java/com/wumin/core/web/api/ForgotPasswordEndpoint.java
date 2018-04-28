package com.wumin.core.web.api;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.security.shiro.CaptchaToken;
import com.wumin.core.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "找回密码接口")
@RestController
public class ForgotPasswordEndpoint {

  @Autowired
  private UserService userService;

  @ApiOperation(value = "密码找回", httpMethod = "POST", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "captcha", value = "验证码", required = true, dataType = "string", paramType = "form")
  })
  @LogIgnore(value = "field", excludes = {"password"})
  @RequestMapping(value = "/api/retrieve", method = RequestMethod.POST)
  public ResponseResult retrieve(String username, String password, String captcha) {
    String phoneCode = (String) SecurityUtils.getSubject().getSession().getAttribute(CaptchaToken.CAPTCHA_SESSION_PHONE);
    String captchaCode = (String) SecurityUtils.getSubject().getSession().getAttribute(CaptchaToken.CAPTCHA_SESSION_KEY);
    if (StringUtils.isEmpty(captchaCode)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "验证码已过期");
    } else if (!captchaCode.equalsIgnoreCase(captcha)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "验证码错误");
    } else if (StringUtils.isEmpty(phoneCode) || !StringUtils.equals(username, phoneCode)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "用户名和验证码不匹配");
    }

    userService.changePassword(username, password);

    return ResponseResult.SUCCEED;
  }

}
