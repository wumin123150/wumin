package com.wumin.core.web.api;

import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.ResponseResult;
import com.wumin.core.entity.Token;
import com.wumin.core.service.TokenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/api/token")
public class TokenEndpoint {

  @Autowired
  private TokenService tokenService;

  @ApiOperation(value = "检查token是否有效", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "/validate/{token}")
  public ResponseResult validateToken(@PathVariable("token") String id, HttpServletRequest request) {
    Token token = tokenService.get(id);
    if (token != null && token.isEnabled() && token.getUser().isEnabled()) {
      return ResponseResult.SUCCEED;
    }

    return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "由于您长时间未登录 请重新登录。");
  }

}
