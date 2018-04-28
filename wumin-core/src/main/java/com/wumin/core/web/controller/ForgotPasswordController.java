package com.wumin.core.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.common.security.shiro.CaptchaToken;
import com.wumin.core.service.UserService;

/**
 * 找回密码Action
 * <p>
 * 用户忘记密码后点击找回密码链接，输入用户名、邮箱和验证码<li>
 * 如果信息正确，系统将发送一封找回密码的邮件，邮件包含一个验证码<li>
 * 如果输入错误或服务器邮箱等信息设置不完整，则给出提示信息<li>
 */
@Api(description = "找回密码管理")
@Controller
public class ForgotPasswordController {

  @Autowired
  private UserService userService;

  @ApiOperation(value = "找回密码页面", httpMethod = "GET")
  @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
  public String retrieve() {
    return "retrieve";
  }

  @ApiOperation(value = "密码找回", httpMethod = "POST")
  @LogIgnore(value = "field", excludes = {"password", "repassword"})
  @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
  public String retrieve(String username, String password, String captcha, ModelMap model,
                         RedirectAttributes redirectAttributes) {
    ObjectError error = null;

    String phoneCode = (String) SecurityUtils.getSubject().getSession()
      .getAttribute(CaptchaToken.CAPTCHA_SESSION_PHONE);
    String captchaCode = (String) SecurityUtils.getSubject().getSession()
      .getAttribute(CaptchaToken.CAPTCHA_SESSION_KEY);
    if (StringUtils.isEmpty(captchaCode)) {
      error = new ObjectError("error", "验证码已过期");
    } else if (!captchaCode.equalsIgnoreCase(captcha)) {
      error = new ObjectError("error", "验证码错误");
    } else if (StringUtils.isEmpty(phoneCode) || !StringUtils.equals(username, phoneCode)) {
      error = new ObjectError("error", "用户名和验证码不匹配");
    }

    if (error != null) {
      model.addAttribute("username", username);

      model.addAttribute("error", error);
      return "retrieve";
    }

    userService.changePassword(username, password);

    redirectAttributes.addFlashAttribute("message", "重置密码成功");
    return "redirect:/login";
  }

}
