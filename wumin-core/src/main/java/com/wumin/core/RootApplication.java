package com.wumin.core;

import com.wumin.common.web.ApplicationCleanListener;
import com.wumin.common.web.FileRepository;
import com.wumin.core.web.BeanMapperRegister;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.Queue;

@SpringBootApplication(scanBasePackages = "com.wumin")
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
//@EnableJms
@EnableCaching
public class RootApplication {

	public static void main(String[] args) {
		SpringApplication.run(RootApplication.class, args);
	}

	@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
		servletListenerRegistrationBean.setListener(new ApplicationCleanListener());
		return servletListenerRegistrationBean;
	}

	@Bean(initMethod = "init")
	BeanMapperRegister beanMapperRegister(){
		return new BeanMapperRegister();
	}

	@Bean
	FileRepository fileRepository(){
		return new FileRepository();
	}

}
