package com.wumin.core.web;

import com.wumin.common.collection.MapUtil;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.net.IPUtil;
import com.wumin.common.net.RequestUtil;
import com.wumin.common.web.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin
@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

  private JsonMapper jsonMapper = new JsonMapper();

  @ExceptionHandler(value = {Exception.class})
  public final ResponseEntity<ResponseResult> handleGeneralException(Exception ex, HttpServletRequest request)
    throws Exception {
    logError(ex, request);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(MediaTypes.JSON_UTF_8));
    ResponseResult result = ResponseResult.create4Exception(ex);

    return new ResponseEntity<ResponseResult>(result, headers, HttpStatus.OK);
  }

  // public void logError(Exception ex) {
  // Map<String, String> map = Maps.newHashMap();
  // map.put("message", ex.getMessage());
  // logger.error(jsonMapper.toJson(map), ex);
  // }

  public void logError(Exception ex, HttpServletRequest request) {
    Map<String, String> map = MapUtil.newHashMap();
    map.put("message", ex.getMessage());
    map.put("from", IPUtil.getIp(request));
    map.put("path", RequestUtil.getLocation(request));
    LOGGER.error(jsonMapper.toJson(map), ex);
  }
}
