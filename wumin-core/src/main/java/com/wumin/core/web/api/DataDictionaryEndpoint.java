package com.wumin.core.web.api;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.core.dto.DataDictionaryDto;
import com.wumin.core.dto.TreeNodeDto;
import com.wumin.core.entity.DataDictionary;
import com.wumin.core.service.DataDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Validation;
import java.util.List;

@Api(description = "数据字典接口")
@RestController
@RequestMapping(value = "/api/dataDictionary")
public class DataDictionaryEndpoint {

  @Autowired
  private DataDictionaryService dataDictionaryService;

  @ApiOperation(value = "根据数据类型和父节点获取数据字典列表", notes = "父节点为空时，获取数据类型下的所有内容", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "typeId", value = "数据类型ID", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "id", value = "父节点ID", dataType = "long", paramType = "query")
  })
  @RequiresRoles("admin")
  @RequestMapping(value = "tree")
  public ResponseResult<List<DataDictionaryDto>> tree(Long typeId, Long id) {
    List<DataDictionary> dataDictionarys = null;
    if (id != null) {
      if(id == 0) {
        dataDictionarys = dataDictionaryService.findByType(typeId);
      } else {
        dataDictionarys = dataDictionaryService.findChildrenByParent(id);
      }
    } else {
      dataDictionarys = dataDictionaryService.findByType(typeId);
    }
    return ResponseResult.createSuccess(dataDictionarys, DataDictionary.class, DataDictionaryDto.class);
  }

  @ApiOperation(value = "数据字典简单分页列表", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "page2")
  public ResponseResult<List<DataDictionaryDto>> page2(PageQuery pageQuery, Long typeId, Long parentId) {
    Page<DataDictionary> page = dataDictionaryService.getPage(pageQuery.getSearchKey(), typeId, parentId, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), DataDictionary.class, DataDictionaryDto.class);
  }

  @ApiOperation(value = "数据字典保存", httpMethod = "POST", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public ResponseResult<DataDictionaryDto> save(DataDictionaryDto dictionaryDto) {
    ComplexResult result = validate(dictionaryDto);
    if (!result.isSuccess()) {
      return ResponseResult.createParamError(result);
    }

    DataDictionary dataDictionary = null;
    if(dictionaryDto.getId() == null) {
      dataDictionary = BeanMapper.map(dictionaryDto, DataDictionary.class);

      if (dataDictionary.getParentId() != null) {
        DataDictionary parent = dataDictionaryService.get(dataDictionary.getParentId());
        if (parent != null) {
          dataDictionary.setGrade(parent.getGrade() + 1);
        }
      } else {
        dataDictionary.setGrade(1);
      }
    } else {
      dataDictionary = dataDictionaryService.get(dictionaryDto.getId());
      dataDictionary = BeanMapper.map(dictionaryDto, dataDictionary, DataDictionaryDto.class, DataDictionary.class);
    }

    dataDictionary = dataDictionaryService.save(dataDictionary);
    return ResponseResult.createSuccess(dataDictionary, DataDictionaryDto.class);
  }

  @ApiOperation(value = "数据字典删除", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") Long id) {
    List<DataDictionary> children = dataDictionaryService.findChildrenByParent(id);
    if(!ListUtil.isEmpty(children)) {
      return ResponseResult.createError(ErrorCodeFactory.ARGS_ERROR_CODE, "请先删除下级数据");
    }

    dataDictionaryService.delete(id);
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "数据字典获取", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "get/{id}")
  public ResponseResult get(@PathVariable("id") Long id) {
    return new ResponseResult<>(dataDictionaryService.get(id));
  }

  /**
   * 判断代码的唯一性
   */
  @ApiOperation(value = "检查数据字典代码是否存在", httpMethod = "GET")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "typeId", value = "数据类型ID", required = true, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "code", value = "数据字典代码", required = true, dataType = "string", paramType = "query")
  })
  @RequestMapping("checkCode")
  public boolean checkCode(Long id, Long typeId, String code) {
    return !dataDictionaryService.existCode(id, typeId, code);
  }

  private ComplexResult validate(DataDictionaryDto dataDictionary) {
    ComplexResult result = FluentValidator.checkAll()
      .on(dataDictionary, new HibernateSupportedValidator<DataDictionaryDto>().setHiberanteValidator(Validation.buildDefaultValidatorFactory().getValidator()))
      .on(dataDictionary, new ValidatorHandler<DataDictionaryDto>() {
        public boolean validate(ValidatorContext context, DataDictionaryDto t) {
          if (dataDictionaryService.existCode(t.getId(), t.getTypeId(), t.getCode())) {
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
