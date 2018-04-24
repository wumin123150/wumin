package com.wumin.common.web.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

/**
 * 长度校验
 */
public class SizeValidator extends ValidatorHandler<String> implements Validator<String> {

  private int min;

  private int max;

  private String message;

  public SizeValidator(int max, String message) {
    this(0, max, message);
  }

  public SizeValidator(int min, int max, String message) {
    this.min = min;
    this.max = max;
    this.message = message;
  }

  @Override
  public boolean validate(ValidatorContext context, String s) {
    if (null == s || s.length() > max || s.length() < min) {
      context.addErrorMsg(message);
      return false;
    }
    return true;
  }

}
