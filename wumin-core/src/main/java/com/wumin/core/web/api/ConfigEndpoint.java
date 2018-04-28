package com.wumin.core.web.api;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.common.mapper.JsonMapper;
import com.wumin.common.persistence.JqGridFilter;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.ConfigDto;
import com.wumin.core.dto.ConfigListDto;
import com.wumin.core.entity.Config;
import com.wumin.core.service.ConfigService;
import com.wumin.core.vo.ConfigVo;
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

@Api(description = "系统配置接口")
@RestController
@RequestMapping(value = "/api/config")
public class ConfigEndpoint {

  @Autowired
  private ConfigService configService;

  static {
    BeanMapper.registerClassMap(Config.class, ConfigDto.class, false, false);
  }

  @ApiOperation(value = "系统配置分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page", method = RequestMethod.GET)
  public ResponseResult<Page<ConfigVo>> page(PageQuery pageQuery, Boolean advance, HttpServletRequest request) {
    Page<Config> page = null;
    if (BooleanUtils.isTrue(advance)) {
      JqGridFilter jqGridFilter = JsonMapper.INSTANCE.fromJson(request.getParameter("filters"), JqGridFilter.class);
      page = configService.getPage(new SearchRequest(jqGridFilter, pageQuery));
    } else {
      page = configService.getPage(new SearchRequest(pageQuery));
    }

    return ResponseResult.createSuccess(page, Config.class, ConfigVo.class);
  }

  @ApiOperation(value = "系统配置简单分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page2", method = RequestMethod.GET)
  public ResponseResult<List<ConfigVo>> page2(PageQuery pageQuery) {
    Page<Config> page = configService.getPage(new SearchRequest(pageQuery));
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), Config.class, ConfigVo.class);
  }

  @ApiOperation(value = "单配置获取", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "键", required = true, dataType = "string", paramType = "path")
  })
  @RequestMapping("get/{code}")
  public ResponseResult<ConfigDto> getByCode(@PathVariable("code") String code) {
    Config config = configService.getByCode(code);
    return ResponseResult.createSuccess(config, ConfigDto.class);
  }

  @ApiOperation(value = "多配置获取", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "键前缀", required = true, dataType = "string", paramType = "path")
  })
  @RequestMapping("find/{code}")
  public ResponseResult<List<ConfigDto>> findByCode(@PathVariable("code") String code) {
    SearchRequest searchRequest = new SearchRequest().addStartWith("code", code + ".");
    List<Config> configs = configService.getList(searchRequest);
    return ResponseResult.createSuccess(configs, Config.class, ConfigDto.class);
  }

  @ApiOperation(value = "配置保存", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public ResponseResult save(ConfigVo configVo) {
    ComplexResult result = validate(configVo);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    Config config = null;
    if(configVo.getId() == null) {
      config = BeanMapper.map(configVo, Config.class);;
    } else {
      config = configService.get(configVo.getId());
      BeanMapper.map(configVo, config, ConfigVo.class, Config.class);
    }

    configService.save(config);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "参数修改", httpMethod = "POST")
  @RequiresRoles("admin")
  @RequestMapping(value = "updateAll", method = RequestMethod.POST)
  public ResponseResult updateAll(ConfigListDto configList) {
    List<ConfigDto> configs = configList.getConfigs();
    if (configs != null) {
      for (ConfigDto configDto : configs) {
        Config config = configService.get(configDto.getId());
        if (config != null) {
          config.setValue(configDto.getValue());
          configService.save(config);
        }
      }
    }

    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "配置删除", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") Long id) {
    configService.delete(id);
    return ResponseResult.SUCCEED;
  }

  /**
   * 判断代码的唯一性
   */
  @ApiOperation(value = "检查配置键是否存在", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "键", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping("checkCode")
  public boolean checkCode(Long id, String code) {
    return !configService.existCode(id, code);
  }

  private ComplexResult validate(ConfigVo config) {
    ComplexResult result = FluentValidator.checkAll()
      .on(config, new HibernateSupportedValidator<ConfigVo>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator()))
      .on(config, new ValidatorHandler<ConfigVo>() {
        public boolean validate(ValidatorContext context, ConfigVo t) {
          if (configService.existCode(t.getId(), t.getCode())) {
            context.addErrorMsg("键已经被使用");
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
