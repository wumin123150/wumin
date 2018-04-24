package com.wumin.common.exception;

import com.wumin.common.dto.ErrorCode;

public class BaseException extends RuntimeException {

  private Integer code;

  public BaseException(Throwable cause) {
    super(cause);
  }

  public BaseException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.code = errorCode.getCode();
  }

  public BaseException(Integer code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public BaseException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  public BaseException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public Integer getCode() {
    return code;
  }

}
