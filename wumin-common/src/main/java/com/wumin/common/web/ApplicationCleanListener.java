package com.wumin.common.web;

import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.wumin.common.concurrent.ThreadLocalCleanUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationCleanListener implements ServletContextListener {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ApplicationCleanListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ThreadLocalCleanUtil.clearThreadLocals();
		/*
		 * 如果数据故驱动是通过应用服务器(tomcat etc...)中配置的<公用>连接池,这里不需要 否则必须卸载Driver
		 * 
		 * 原因: DriverManager是System classloader加载的, Driver是webappclassloader加载的,
		 * driver保存在DriverManager中,在reload过程中,由于system
		 * classloader不会销毁,driverManager就一直保持着对driver的引用,
		 * driver无法卸载,与driver关联的其他类
		 * ,例如DataSource,jdbcTemplate,dao,service....都无法卸载
		 */
		try {
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("clean jdbc Driver......");
			for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
				Driver driver = (Driver) e.nextElement();
				if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
					DriverManager.deregisterDriver(driver);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception cleaning up java.sql.DriverManager's driver: ", e);
		}

		try {
			Class ConnectionImplClass = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.ConnectionImpl");
			if (ConnectionImplClass != null && ConnectionImplClass.getClassLoader() == getClass().getClassLoader()) {
				Field f = ConnectionImplClass.getDeclaredField("cancelTimer");
				f.setAccessible(true);
				Timer timer = (Timer) f.get(null);
				timer.cancel();
			}
		} catch (java.lang.ClassNotFoundException e1) {
			// do nothing
		} catch (Exception e) {
			LOGGER.error("Exception cleaning up MySQL cancellation timer: ", e);
		}

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}

}
