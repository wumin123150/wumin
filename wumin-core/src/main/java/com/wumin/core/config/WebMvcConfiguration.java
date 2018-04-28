package com.wumin.core.config;

import com.wumin.core.web.ContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Autowired
  private ContextInterceptor contextInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(contextInterceptor).addPathPatterns("/**");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**").allowedMethods("POST", "GET");
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.TEXT_HTML)
      .mediaType("json", MediaType.APPLICATION_JSON_UTF8)
      .mediaType("xml", MediaType.APPLICATION_XML);
  }


  @Override
  public void addViewControllers(ViewControllerRegistry registry) {

  }
}
