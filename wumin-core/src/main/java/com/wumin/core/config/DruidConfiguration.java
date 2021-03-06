package com.wumin.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DruidConfiguration {
  @Value("${spring.datasource.url}")
  private String dbUrl;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  @Value("${spring.datasource.driverClassName}")
  private String driverClassName;
  @Value("${spring.datasource.initialSize}")
  private int initialSize;
  @Value("${spring.datasource.minIdle}")
  private int minIdle;
  @Value("${spring.datasource.maxActive}")
  private int maxActive;
  @Value("${spring.datasource.maxWait}")
  private int maxWait;
  @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
  private int timeBetweenEvictionRunsMillis;
  @Value("${spring.datasource.minEvictableIdleTimeMillis}")
  private int minEvictableIdleTimeMillis;
  @Value("${spring.datasource.validationQuery}")
  private String validationQuery;
  @Value("${spring.datasource.testWhileIdle}")
  private boolean testWhileIdle;
  @Value("${spring.datasource.testOnBorrow}")
  private boolean testOnBorrow;
  @Value("${spring.datasource.testOnReturn}")
  private boolean testOnReturn;
  @Value("${spring.datasource.poolPreparedStatements}")
  private boolean poolPreparedStatements;
  @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
  private int maxPoolPreparedStatementPerConnectionSize;

  @Value("${spring.datasource.removeAbandoned}")
  private boolean removeAbandoned;
  @Value("${spring.datasource.removeAbandonedTimeout}")
  private int removeAbandonedTimeout;
  @Value("${spring.datasource.logAbandoned}")
  private boolean logAbandoned;

  @Value("${spring.datasource.connectionProperties}")
  private String connectionProperties;
  @Value("${spring.datasource.useGlobalDataSourceStat}")
  private boolean useGlobalDataSourceStat;
  @Value("${spring.datasource.filters}")
  private String filters;

  @Bean     //声明其为Bean实例
  @Primary  //在同样的DataSource中，首先使用被标注的DataSource
  public DataSource dataSource(){
    DruidDataSource datasource = new DruidDataSource();
    datasource.setUrl(this.dbUrl);
    datasource.setUsername(username);
    datasource.setPassword(password);
    datasource.setDriverClassName(driverClassName);

    //configuration
    datasource.setInitialSize(initialSize);
    datasource.setMinIdle(minIdle);
    datasource.setMaxActive(maxActive);
    datasource.setMaxWait(maxWait);
    datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    datasource.setValidationQuery(validationQuery);
    datasource.setTestWhileIdle(testWhileIdle);
    datasource.setTestOnBorrow(testOnBorrow);
    datasource.setTestOnReturn(testOnReturn);
    datasource.setPoolPreparedStatements(poolPreparedStatements);
    datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

    datasource.setRemoveAbandoned(removeAbandoned);
    datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    datasource.setLogAbandoned(logAbandoned);

    datasource.setConnectionProperties(connectionProperties);
    datasource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
    try {
      datasource.setFilters(filters);
    } catch (SQLException e) {
      System.err.println("druid configuration initialization filter: "+ e);
    }
    return datasource;
  }

  @Bean
  public ServletRegistrationBean druidServlet() {
    ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    // IP白名单
    registration.addInitParameter("allow", "127.0.0.1");
    // IP黑名单(共同存在时，deny优先于allow)
    registration.addInitParameter("deny", "192.168.1.100");
    //控制台管理用户
    registration.addInitParameter("loginUsername", "druid");
    registration.addInitParameter("loginPassword", "druid");
    //是否能够重置数据 禁用HTML页面上的“Reset All”功能
    registration.addInitParameter("resetEnable", "false");
    return registration;
  }

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean(new WebStatFilter());
    registration.addUrlPatterns("/*");
    registration.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/webjars/*,/static/*");
    registration.addInitParameter("sessionStatMaxCount", "2000");
    registration.addInitParameter("sessionStatEnable", "true");
    registration.addInitParameter("principalSessionName", "session_user_key");
    registration.addInitParameter("profileEnable", "true");
    return registration;
  }

}
