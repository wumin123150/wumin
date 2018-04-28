package com.wumin.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "系统配置")
public class ConfigVo {

  private Long id;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称最大长度为64位")
  private String name;
  @ApiModelProperty(value = "键", required = true)
  @NotBlank(message = "键不能为空")
  @Size(max = 128, message = "键最大长度为128位")
  private String code;
  @ApiModelProperty(value = "值", required = true)
  @NotBlank(message = "值不能为空")
  @Size(max = 128, message = "值最大长度为128位")
  private String value;
  @ApiModelProperty(value = "类型(0:Input输入框,1:Textarea文本框,2:Radio单选框,3:Checkbox多选框,4:Select单选框,5:Select多选框)", required = true)
  @NotNull(message = "类型不能为空")
  private Integer type;
  @ApiModelProperty(value = "参数")
  private String params;
  @ApiModelProperty(value = "是否可修改(0:不可修改,1:可修改)", required = true)
  private Boolean editable = false;
  @ApiModelProperty(value = "备注")
  @Size(max = 128, message = "备注最大长度为128位")
  private String remark;

}
