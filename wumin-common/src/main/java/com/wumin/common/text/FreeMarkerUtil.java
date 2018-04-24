package com.wumin.common.text;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.wumin.common.base.ExceptionUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * FreeMark工具类
 * 
 * @author walker
 */
public class FreeMarkerUtil {

	/**
	 * 渲染模板字符串。
	 */
	public static String renderString(String templateString, Map<String, ?> model) {
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
			cfg.setClassicCompatible(true);//处理空值为空字符串
			StringWriter result = new StringWriter();
			Template t = new Template("default", new StringReader(templateString), cfg);
			t.process(model, result);
			return result.toString();
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

	/**
	 * 渲染Template文件.
	 */
	public static String renderTemplate(Template template, Object model) {
		try {
			StringWriter result = new StringWriter();
			template.process(model, result);
			return result.toString();
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

	/**
	 * 创建默认配置，设定模板目录.
	 */
	public static Configuration buildConfiguration(String directory) throws IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		Resource path = new DefaultResourceLoader().getResource(directory);
		cfg.setDirectoryForTemplateLoading(path.getFile());
		cfg.setClassicCompatible(true);//处理空值为空字符串
		return cfg;
	}
}
