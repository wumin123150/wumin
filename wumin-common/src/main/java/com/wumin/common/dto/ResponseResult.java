package com.wumin.common.dto;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.wumin.common.base.ExceptionUtil;
import com.wumin.common.exception.BaseException;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.reflect.ClassUtil;
import com.wumin.common.reflect.ReflectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public class ResponseResult<T> {

  public final static int SUCCESS_CODE = 0;

  public final static ResponseResult SUCCEED = new ResponseResult<>();
  //    public final static ResponseResult LOGIN = new ResponseResult<>(ERR_LOGIN, "login");
  public final static ResponseResult UNAUTHORIZED = createError(ErrorCodeFactory.UNAUTHORIZED);//401
  public final static ResponseResult FORBIDDEN = createError(ErrorCodeFactory.FORBIDDEN);//403

  private boolean success;
  private int code;
  private String message;
  private String debug;
  private long total;
  private T data;

  public ResponseResult() {
    this("success");
  }

  public ResponseResult(T data) {
    this();
    setData(data);
  }

  public ResponseResult(T data, Long total) {
    this();
    setData(data);
    setTotal(total);
  }

  public ResponseResult(String message) {
    this.success = true;
    this.code = SUCCESS_CODE;
    this.message = message;
  }

  public static <T> ResponseResult<T> createSuccess(T data) {
    return new ResponseResult(data);
  }

  public static <T> ResponseResult<List<T>> createSuccess(List<T> sourceList) {
    return new ResponseResult(sourceList, Long.valueOf(sourceList.size()));
  }

  public static <T> ResponseResult<List<T>> createSuccess(List<T> sourceList, Long total) {
    return new ResponseResult(sourceList, total);
  }

  public static <S, T> ResponseResult<T> createSuccess(S data, Class<T> targetClass) {
    return new ResponseResult(BeanMapper.map(data, targetClass));
  }

  public static <S, T> ResponseResult<List<T>> createSuccess(List<S> sourceList, Class<S> sourceClass, Class<T> targetClass) {
    return new ResponseResult(BeanMapper.mapList(sourceList, sourceClass, targetClass), Long.valueOf(sourceList.size()));
  }

  public static <S, T> ResponseResult<List<T>> createSuccess(List<S> sourceList, Long total, Class<S> sourceClass, Class<T> targetClass) {
    return new ResponseResult(BeanMapper.mapList(sourceList, sourceClass, targetClass), total);
  }

  public static <S, T> ResponseResult<Page<T>> createSuccess(Page<S> sourcePage, Class<S> sourceClass, Class<T> targetClass) {
    List<T> content = BeanMapper.mapList(sourcePage.getContent(), sourceClass, targetClass);
    return new ResponseResult(new PageImpl(content, (Pageable) ReflectionUtil.getProperty(sourcePage, "pageable"), sourcePage.getTotalElements()));
  }

  public ResponseResult(ErrorCode errorCode) {
    this.success = false;
    this.code = errorCode.getCode();
    this.message = errorCode.getMessage();
  }

  public ResponseResult(Integer code, String message) {
    this.success = false;
    this.code = code;
    this.message = message;
  }

  public static ResponseResult createError(ErrorCode errorCode) {
    return new ResponseResult(errorCode);
  }

  public static ResponseResult createError(Integer code, String message) {
    return new ResponseResult(code, message);
  }

  public static ResponseResult createParamError(String message) {
    return new ResponseResult(ErrorCodeFactory.ARGS_ERROR_CODE, message);
  }

  public static ResponseResult createParamError(ComplexResult complexResult) {
    StringBuilder debugBuilder = new StringBuilder();
    StringBuilder builder = new StringBuilder();
    List<ValidationError> errors = complexResult.getErrors();
    for(ValidationError error : errors) {
      debugBuilder.append(";").append(error.toString());
      builder.append(";").append(error.getErrorMsg());
    }

    if(builder.length() > 0) {
      ResponseResult result = createError(ErrorCodeFactory.ARGS_ERROR_CODE, builder.substring(1).toString());
      result.setDebug(debugBuilder.substring(1).toString());
      return result;
    } else {
      return createError(ErrorCodeFactory.BAD_REQUEST);
    }
  }

  public static ResponseResult createParamError(BindingResult bindingResult) {
    StringBuilder debugBuilder = new StringBuilder();
    StringBuilder builder = new StringBuilder();
    List<ObjectError> errors = bindingResult.getAllErrors();
    for(ObjectError error : errors) {
      debugBuilder.append(";").append(error.toString());
      builder.append(";").append(error.getDefaultMessage());
    }

    if(builder.length() > 0) {
      ResponseResult result = createError(ErrorCodeFactory.ARGS_ERROR_CODE, builder.substring(1).toString());
      result.setDebug(debugBuilder.substring(1).toString());
      return result;
    } else {
      return createError(ErrorCodeFactory.BAD_REQUEST);
    }
  }

  public static ResponseResult createParamError(BindingResult bindingResult, String message) {
    ResponseResult result = createError(ErrorCodeFactory.ARGS_ERROR_CODE, message);

    StringBuilder builder = new StringBuilder();
    List<ObjectError> errors = bindingResult.getAllErrors();
    for(ObjectError error : errors) {
      builder.append(";").append(error.toString());
    }

    if(builder.length() > 0) {
      result.setDebug(builder.substring(1).toString());
    }

    return result;
  }

  public static ResponseResult create4Exception(Exception ex) {
    if (ex instanceof BaseException && ((BaseException) ex).getCode() != null) {
      ResponseResult result = createError(((BaseException) ex).getCode(), ex.getMessage());
      result.setDebug(ExceptionUtil.toStringWithLocation(ex, ClassUtil.getPackageName(ResponseResult.class.getName(), 2)));
      return result;
    }

    if (ex instanceof BindException) {
      ResponseResult result = createParamError(((BindException) ex).getBindingResult());
      return result;
    }

    if (ex instanceof MethodArgumentNotValidException) {
      ResponseResult result = createParamError(((MethodArgumentNotValidException) ex).getBindingResult());
      return result;
    }

    ResponseResult result = createError(ErrorCodeFactory.INTERNAL_SERVER_ERROR);
    result.setDebug(ExceptionUtil.toStringWithLocation(ex, ClassUtil.getPackageName(ResponseResult.class.getName(), 2)));
    return result;
  }

  public boolean isSuccess() {
    return success;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDebug() {
    return debug;
  }

  public void setDebug(String debug) {
    this.debug = debug;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

}
