package com.wumin.core.web.api;

import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.exception.BaseException;
import com.wumin.common.security.shiro.CaptchaToken;
import com.wumin.common.text.TextValidator;
import com.wumin.core.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Api(description = "验证码接口")
@RestController
public class CaptchaEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaEndpoint.class);

  @Value("${sms.content}")
  private String smsContent;
  @Autowired
  UserService userService;

  @ApiOperation(value = "检查验证码是否正确", notes = "简单检查时，无需传递手机", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "phone", value = "手机", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "captcha", value = "验证码", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping(value = "/api/captcha/check")
  public Boolean checkCaptcha(String phone, String captcha) {
    String phoneCode = (String) SecurityUtils.getSubject().getSession().getAttribute(CaptchaToken.CAPTCHA_SESSION_PHONE);
    String captchaCode = (String) SecurityUtils.getSubject().getSession().getAttribute(CaptchaToken.CAPTCHA_SESSION_KEY);
    if (StringUtils.isNotEmpty(phone)) {
      return StringUtils.isNotEmpty(captchaCode) && StringUtils.equalsIgnoreCase(captcha, captchaCode) && StringUtils.equals(phone, phoneCode);
    } else {
      return StringUtils.isNotEmpty(captchaCode) && StringUtils.equalsIgnoreCase(captcha, captchaCode);
    }
  }

  @ApiOperation(value = "发送验证码", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "phone", value = "手机", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping(value = "/api/captcha/send")
  public ResponseResult sendCaptcha(String phone) {
    if (StringUtils.isNotEmpty(phone)) {
      send(phone, null);
    } else {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "手机不存在");
    }

    return ResponseResult.SUCCEED;
  }

  private boolean send(String phone, String content) {
    if (!TextValidator.isMobileSimple(phone)) {
      throw new BaseException(ErrorCodeFactory.ARGS_ERROR_CODE, "手机号码不正确！");
    }

    Integer time = (Integer) SecurityUtils.getSubject().getSession().getAttribute(CaptchaToken.CAPTCHA_SESSION_TIME);
    if (time == null) {
      SecurityUtils.getSubject().getSession().setAttribute(CaptchaToken.CAPTCHA_SESSION_TIME, CaptchaToken.DEFAULT_TIME - 1);
    } else if (time > 0) {
      SecurityUtils.getSubject().getSession().setAttribute(CaptchaToken.CAPTCHA_SESSION_TIME, --time);
    } else {
      throw new BaseException(ErrorCodeFactory.ARGS_ERROR_CODE, "短信运营商规定，短期内不能向同手机发送多条短信，请1小时后再试！");
    }

    String captcha = randomNum(99999, 6);
    SecurityUtils.getSubject().getSession().setAttribute(CaptchaToken.CAPTCHA_SESSION_PHONE, phone);
    SecurityUtils.getSubject().getSession().setAttribute(CaptchaToken.CAPTCHA_SESSION_KEY, captcha);

    if (StringUtils.isEmpty(content)) {
      content = StringUtils.replace(smsContent, "{captcha}", captcha);
    }

    boolean result = false;//TODO: 使用阿里短信
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("验证码:{}", captcha);
    }
    return result;
  }

  private static String randomNum(int max, int length) {
    Random random = new Random();
    int value = random.nextInt(max);
    return StringUtils.leftPad(String.valueOf(value), length, "0");
  }

}
