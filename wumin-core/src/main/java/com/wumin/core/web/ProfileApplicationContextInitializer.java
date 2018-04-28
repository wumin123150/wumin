package com.wumin.core.web;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ProfileApplicationContextInitializer implements
  ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileApplicationContextInitializer.class);

  public static String profile;

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    try {
      ClassPathResource resource = new ClassPathResource("application.properties");
      Properties properties = PropertiesLoaderUtils.loadProperties(resource);

      profile = properties.getProperty("spring.profile");

      applicationContext.getEnvironment().setActiveProfiles(profile.split(","));
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Active spring profile: {}", profile);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
