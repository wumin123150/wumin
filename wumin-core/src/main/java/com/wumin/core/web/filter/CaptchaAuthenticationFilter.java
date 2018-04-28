package com.wumin.core.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.util.WebUtils;

import com.wumin.common.security.shiro.CaptchaToken;

/**
 * 自定义登录认证filter
 */
public class CaptchaAuthenticationFilter extends PasswordAuthenticationFilter {

  public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
  private String captchaParam = DEFAULT_CAPTCHA_PARAM;

  protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
    String captcha = getCaptcha(request);

    if (StringUtils.isEmpty(captcha)) {
      return super.createToken(request, response);
    } else {
      String username = getUsername(request);
      String host = getHost(request);
      return new CaptchaToken(username, captcha, host);
    }
  }

  public String getCaptchaParam() {
    return captchaParam;
  }

  protected String getCaptcha(ServletRequest request) {
    return WebUtils.getCleanParam(request, getCaptchaParam());
  }

}
