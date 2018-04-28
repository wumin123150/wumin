package com.wumin.core.web.api;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.AnnexDto;
import com.wumin.core.entity.Annex;
import com.wumin.core.service.AnnexService;
import com.wumin.core.vo.AnnexVo;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Api(description = "附件接口")
@RestController
@RequestMapping(value = "/api/annex")
public class AnnexEndpoint {

  @Autowired
  private AnnexService annexService;

  @ApiOperation(value = "附件分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page")
  public ResponseResult<Page<AnnexDto>> page(PageQuery pageQuery, Boolean advance,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime,
                                          HttpServletRequest request) {
    Page<Annex> page = null;
    if (BooleanUtils.isTrue(advance)) {
      JqGridFilter jqGridFilter = JsonMapper.INSTANCE.fromJson(request.getParameter("filters"), JqGridFilter.class);
      page = annexService.getPage(new SearchRequest(jqGridFilter, pageQuery));
    } else {
      page = annexService.getPage(pageQuery.getSearchKey(), startTime, endTime, pageQuery.buildPageRequest());
    }
    return ResponseResult.createSuccess(page, Annex.class, AnnexDto.class);
  }

  @ApiOperation(value = "附件简单分页列表", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm)", dataType = "date", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "page2")
  public ResponseResult<List<AnnexDto>> page2(PageQuery pageQuery,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime) {
    Page<Annex> page = annexService.getPage(pageQuery.getSearchKey(), startTime, endTime, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), Annex.class, AnnexDto.class);
  }

  @ApiOperation(value = "单附件保存", httpMethod = "POST")
  @RequestMapping(value = "save")
  public ResponseResult<AnnexVo> save(@RequestParam MultipartFile file) throws IOException {
    if (file != null && !file.isEmpty()) {
      Annex annex = annexService.save(file.getBytes(), file.getOriginalFilename(), Annex.PATH_TEMP, null, Annex.OBJECT_TYPE_TEMP);
      return ResponseResult.createSuccess(annex, AnnexVo.class);
    }
    return ResponseResult.createParamError("请上传文件");
  }

  @ApiOperation(value = "多附件保存", httpMethod = "POST")
  @RequestMapping(value = "batch/save")
  public ResponseResult<List<AnnexVo>> saveInBatch(@RequestParam MultipartFile[] files) throws IOException {
    List<Annex> annexs = ListUtil.newArrayList();
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        Annex annex = annexService.save(files[i].getBytes(), files[i].getOriginalFilename(), Annex.PATH_TEMP, null, Annex.OBJECT_TYPE_TEMP);
        annexs.add(annex);
      }
      return ResponseResult.createSuccess(annexs, Annex.class, AnnexVo.class);
    }
    return ResponseResult.createParamError("请上传文件");
  }

  @ApiOperation(value = "附件删除", httpMethod = "GET")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") String id) {
    annexService.delete(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "附件批量删除", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "batch/delete")
  public ResponseResult deleteInBatch(@RequestParam(value = "ids", required = false) String[] ids) {
    annexService.deleteInBatch(ids);
    return ResponseResult.SUCCEED;
  }

}
