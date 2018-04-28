package com.wumin.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "角色")
public class RoleVo {

  private Long id;
  @ApiModelProperty(value = "编码", required = true)
  @NotBlank(message = "代码不能为空")
  @Size(max = 64, message = "代码长度必须在1到64位之间")
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "名称不能为空")
  @Size(max = 64, message = "名称长度必须在1到64位之间")
  private String name;
  @ApiModelProperty(value = "权限")
  private List<Long> permissions;

}
