package com.wumin.common.web.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

/**
 * 校验不为null
 */
public class NotNullValidator extends ValidatorHandler<String> implements Validator<String> {

  private String message;

  public NotNullValidator(String message) {
    this.message = message;
  }

  @Override
  public boolean validate(ValidatorContext context, String s) {
    if (null == s) {
      context.addErrorMsg(message);
      return false;
    }
    return true;
  }

}
