package com.wumin.core.web.api;

import com.wumin.common.base.annotation.LogIgnore;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.LogDto;
import com.wumin.core.entity.Log;
import com.wumin.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(description = "日志接口")
@RestController
@RequestMapping(value = "/api/log")
public class LogEndpoint {

  @Autowired
  private LogService logService;

  @ApiOperation(value = "日志分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query")
  })
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping(value = "page")
  public ResponseResult<Page<LogDto>> page(PageQuery pageQuery, Boolean advance,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime,
                                           HttpServletRequest request) {
    Page<Log> page = null;
    if (BooleanUtils.isTrue(advance)) {
      JqGridFilter jqGridFilter = JsonMapper.INSTANCE.fromJson(request.getParameter("filters"), JqGridFilter.class);
      page = logService.getPage(new SearchRequest(jqGridFilter, pageQuery));
    } else {
      page = logService.getPage(pageQuery.getSearchKey(), startTime, endTime, pageQuery.buildPageRequest());
    }
    return ResponseResult.createSuccess(page, Log.class, LogDto.class);
  }

  @ApiOperation(value = "日志简单分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query")
  })
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping(value = "page2")
  public ResponseResult<List<LogDto>> page2(PageQuery pageQuery,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime) {
    Page<Log> page = logService.getPage(pageQuery.getSearchKey(), startTime, endTime, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), Log.class, LogDto.class);
  }

  @ApiOperation(value = "日志获取", httpMethod = "GET", produces = "application/json")
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping("get/{id}")
  public ResponseResult<LogDto> get(@PathVariable("id") String id) {
    Log log = logService.get(id);
    return ResponseResult.createSuccess(log, LogDto.class);
  }

  @ApiOperation(value = "日志删除", httpMethod = "GET", produces = "application/json")
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") String id) {
    logService.delete(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "日志批量删除", httpMethod = "POST")
  @LogIgnore
  @RequiresRoles("admin")
  @RequestMapping(value = "batch/delete")
  public ResponseResult deleteInBatch(@RequestParam(value = "ids", required = false) String[] ids) {
    logService.deleteInBatch(ids);
    return ResponseResult.SUCCEED;
  }

}
