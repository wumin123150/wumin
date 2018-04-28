package com.wumin.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "数据字典")
public class DataDictionaryDto {

  private Long id;
  @ApiModelProperty(value = "类型ID", required = true)
  @NotNull(message = "类型不能为空")
  private Long typeId;
  @ApiModelProperty(value = "代码", required = true)
  @NotBlank(message = "代码不能为空")
  @Size(max = 64, message = "代码最大长度为64位")
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称最大长度为64位")
  private String name;
  @ApiModelProperty(value = "序号", required = true)
  @NotNull(message = "序号不能为空")
  @Range(min = 0, max = 10000, message = "序号必须在0到10000之间")
  private Integer priority;
  @ApiModelProperty(value = "层级")
  private Integer grade;
  @ApiModelProperty(value = "父ID")
  private Long parentId;
  @ApiModelProperty(value = "备注")
  @Size(max = 128, message = "备注最大长度为128位")
  private String remark;

}
