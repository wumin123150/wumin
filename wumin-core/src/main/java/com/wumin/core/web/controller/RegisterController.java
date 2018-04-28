package com.wumin.core.web.controller;

import com.wumin.common.net.IPUtil;
import com.wumin.core.entity.User;
import com.wumin.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class RegisterController {

  @Autowired
  private UserService userService;

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public String register() {
    return "register";
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String register(String mobile, String name, String password, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
    if (userService.existLoginName(null, mobile)) {
      model.addAttribute("mobile", mobile);
      model.addAttribute("name", name);
      model.addAttribute("error", "手机号码已注册");
      return "register";
    }

    User user = new User();
    user.setLoginName(mobile);
    user.setName(name);
    user.setPlainPassword(password);
    user.setMobile(mobile);
    user.setRegisterTime(new Date());
    user.setRegisterIp(IPUtil.getIp(request));
    user.setStatus(User.STATUS_ACTIVE);

    userService.save(user);

    redirectAttributes.addFlashAttribute("message", "用户注册成功");
    return "redirect:/login";
  }

}
