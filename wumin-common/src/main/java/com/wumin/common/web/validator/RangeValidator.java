package com.wumin.common.web.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

/**
 * 校验数字大小
 */
public class RangeValidator extends ValidatorHandler<Integer> implements Validator<Integer> {

  private long min;

  private long max;

  private String message;

  public RangeValidator(int max, String message) {
    this(0, max, message);
  }

  public RangeValidator(int min, int max, String message) {
    this.min = min;
    this.max = max;
    this.message = message;
  }

  @Override
  public boolean validate(ValidatorContext context, Integer integer) {
    if (null == integer || integer.intValue() > max || integer.intValue() < min) {
      context.addErrorMsg(message);
      return false;
    }
    return true;
  }

}
