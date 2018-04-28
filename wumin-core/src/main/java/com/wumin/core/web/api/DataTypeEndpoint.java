package com.wumin.core.web.api;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.DataTypeDto;
import com.wumin.core.entity.DataType;
import com.wumin.core.service.DataTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import java.util.List;

@Api(description = "数据类型接口")
@RestController
@RequestMapping(value = "/api/dataType")
public class DataTypeEndpoint {

  @Autowired
  private DataTypeService dataTypeService;

  @ApiOperation(value = "数据类型分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page", method = RequestMethod.GET)
  public ResponseResult<Page<DataTypeDto>> page(PageQuery pageQuery, Boolean advance, HttpServletRequest request) {
    Page<DataType> page = null;
    if (BooleanUtils.isTrue(advance)) {
      JqGridFilter jqGridFilter = JsonMapper.INSTANCE.fromJson(request.getParameter("filters"), JqGridFilter.class);
      page = dataTypeService.getPage(new SearchRequest(jqGridFilter, pageQuery));
    } else {
      page = dataTypeService.getPage(new SearchRequest(pageQuery, "code", "name"));
    }

    return ResponseResult.createSuccess(page, DataType.class, DataTypeDto.class);
  }

  @ApiOperation(value = "数据类型简单分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page2", method = RequestMethod.GET)
  public ResponseResult<List<DataTypeDto>> page2(PageQuery pageQuery) {
    Page<DataType> page = dataTypeService.getPage(new SearchRequest(pageQuery, "code", "name"));
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), DataType.class, DataTypeDto.class);
  }

  @ApiOperation(value = "数据类型保存", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public ResponseResult save(DataTypeDto dataTypeDto) {
    ComplexResult result = validate(dataTypeDto);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    DataType dataType = null;
    if(dataTypeDto.getId() == null) {
      dataType = BeanMapper.map(dataTypeDto, DataType.class);;
    } else {
      dataType = dataTypeService.get(dataTypeDto.getId());
      BeanMapper.map(dataTypeDto, dataType, DataTypeDto.class, DataType.class);
    }

    dataTypeService.save(dataType);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "数据类型删除", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") Long id) {
    dataTypeService.delete(id);
    return ResponseResult.SUCCEED;
  }

  /**
   * 判断代码的唯一性
   */
  @ApiOperation(value = "检查数据类型代码是否存在", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "代码", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping("checkCode")
  public boolean checkCode(Long id, String code) {
    return !dataTypeService.existCode(id, code);
  }

  private ComplexResult validate(DataTypeDto dataType) {
    ComplexResult result = FluentValidator.checkAll()
      .on(dataType, new HibernateSupportedValidator<DataTypeDto>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator()))
      .on(dataType, new ValidatorHandler<DataTypeDto>() {
        public boolean validate(ValidatorContext context, DataTypeDto t) {
          if (dataTypeService.existCode(t.getId(), t.getCode())) {
            context.addErrorMsg("代码已经被使用");
            return false;
          }
          return true;
        }
      })
      .doValidate()
      .result(ResultCollectors.toComplex());
    return result;
  }
}
