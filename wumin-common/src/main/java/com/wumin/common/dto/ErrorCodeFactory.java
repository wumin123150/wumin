package com.wumin.common.dto;

public class ErrorCodeFactory {

  public static final Integer BAD_REQUEST_CODE = 400;
  public static final Integer UNAUTHORIZED_CODE = 401;
  public static final Integer FORBIDDEN_CODE = 403;
  public static final Integer INTERNAL_SERVER_ERROR_CODE = 500;

  /**参数错误或者不全**/
  public static final Integer ARGS_ERROR_CODE = 10001;

  //常见的错误
  public static final ErrorCode BAD_REQUEST = new ErrorCode(400, "请求错误");
  public static final ErrorCode UNAUTHORIZED = new ErrorCode(401, "未经许可");
  public static final ErrorCode FORBIDDEN = new ErrorCode(403, "禁止访问");
  public static final ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统错误");

}
