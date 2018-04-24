package com.wumin.core.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan("com.wumin.*.web")
public class SwaggerConfiguration {

  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo())
      .useDefaultResponseMessages(false)
      .select()
      .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
      .paths(PathSelectors.any())
      .build()
      .ignoredParameterTypes(ApiIgnore.class, Model.class, ModelMap.class, RedirectAttributes.class);
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
      .title("自动生成的接口文档")
//                .description("更多内容请关注：http://www.abc.com")
      .contact("system")
      .version("1.0")
      .build();
  }

}
