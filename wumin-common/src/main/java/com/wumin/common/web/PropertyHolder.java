package com.wumin.common.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyHolder extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertiesMap = new HashMap<String, String>();

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		for (String key : props.stringPropertyNames()) {
			String value = props.getProperty(key);
			propertiesMap.put(key, value);
		}
	}

	public static String getProperty(String name) {
		return propertiesMap.get(name);
	}
	
	public static String getProperty(String name, String defaultValue) {
		String value = propertiesMap.get(name);
		return value == null ? defaultValue : value;
	}
	
	public static String getEmptyProperty(String name) {
		String value = propertiesMap.get(name);
		return value == null ? "" : value;
	}
	
	public static boolean getBoolProperty(String name) {
		String value = getProperty(name);
		return Boolean.valueOf(value);
	}
	
	public static Integer getIntegerProperty(String name) {
		String value = getProperty(name);
		if(NumberUtils.isCreatable(value))
			return NumberUtils.createInteger(value);
		else 
			return null;
	}
	
	public static int getIntProperty(String name) {
		return getIntProperty(name, 0);
	}
	
	public static int getIntProperty(String name, int defaultValue) {
		String value = getProperty(name);
		return NumberUtils.toInt(value, defaultValue);
	}
	
	public static long getLongProperty(String name) {
		return getLongProperty(name, 0);
	}
	
	public static long getLongProperty(String name, long defaultValue) {
		String value = getProperty(name);
		return NumberUtils.toLong(value, defaultValue);
	}

}
