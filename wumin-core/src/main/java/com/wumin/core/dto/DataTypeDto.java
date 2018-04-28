package com.wumin.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "数据类型")
public class DataTypeDto {

  private Long id;
  @ApiModelProperty(value = "编码", required = true)
  @NotBlank(message = "代码不能为空")
  @Size(max = 64, message = "代码最大长度为64位")
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称最大长度为64位")
  private String name;
  @ApiModelProperty(value = "层级", required = true)
  @NotNull(message = "层级不能为空")
  private Integer grade;
  @ApiModelProperty(value = "是否可修改数据(0:不可修改,1:可修改)", required = true)
  private Boolean editable = false;
  @ApiModelProperty(value = "备注")
  @Size(max = 128, message = "备注最大长度为128位")
  private String remark;

}
