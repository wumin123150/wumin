package com.wumin.common.persistence;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.sql.DataSource;

import com.wumin.common.entity.IdEntity;
import com.wumin.common.entity.UuidEntity;
import com.wumin.common.reflect.ClassUtil;
import com.wumin.common.reflect.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.SQLServer2008Dialect;

public class Hibernates {

	/**
	 * Initialize the lazy property value.
	 * 
	 * eg. Hibernates.initLazyProperty(user.getGroups()); 
	 */
	public static void initLazyProperty(Object proxyedPropertyValue) {
		Hibernate.initialize(proxyedPropertyValue);
	}

	/**
	 * 从DataSoure中取出connection, 根据connection的metadata中的jdbcUrl判断Dialect类型.
	 * 仅支持Oracle, H2, MySql, PostgreSql, SQLServer，如需更多数据库类型，请仿照此类自行编写。
	 */
	public static String getDialect(DataSource dataSource) {
		String jdbcUrl = getJdbcUrlFromDataSource(dataSource);

		// 根据jdbc url判断dialect
		if (StringUtils.contains(jdbcUrl, ":h2:")) {
			return H2Dialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
			return MySQL5InnoDBDialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
			return Oracle10gDialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":postgresql:")) {
			return PostgreSQL82Dialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":sqlserver:")) {
			return SQLServer2008Dialect.class.getName();
		} else {
			throw new IllegalArgumentException("Unknown Database of " + jdbcUrl);
		}
	}

	private static String getJdbcUrlFromDataSource(DataSource dataSource) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (connection == null) {
				throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
			}
			return connection.getMetaData().getURL();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get database url", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ignore) {
					ignore.printStackTrace();
				}
			}
		}
	}

	public static <T> T lazyFields(T entity, String... fieldNames) {
		if(entity == null)
			return entity;

		for (String fieldName : fieldNames) {
			if(StringUtils.isNotEmpty(fieldName)) {
				Field field = ClassUtil.getAccessibleField(entity.getClass(), fieldName);
				entity = lazyField(entity, field);
			}
		}

		return entity;
	}

	private static <T> T lazyField(T entity, Field field) {
		if(entity == null || field == null)
			return entity;

		Object value = ReflectionUtil.getFieldValue(entity, field);
		if(value == null)
			return entity;

		if(value instanceof IdEntity) {
			Long id = ((IdEntity)value).getId();
			if(id != null) {
				Object lazyValue = ReflectionUtil.invokeConstructor(field.getType());
				ReflectionUtil.setProperty(lazyValue, "id", id);
				ReflectionUtil.setField(entity, field, lazyValue);
			} else {
				ReflectionUtil.setField(entity, field, null);
			}
		}
		if(value instanceof UuidEntity) {
			String id = ((UuidEntity)value).getId();
			if(StringUtils.isNotEmpty(id)) {
				Object lazyValue = ReflectionUtil.invokeConstructor(field.getType());
				ReflectionUtil.setProperty(lazyValue, "id", id);
				ReflectionUtil.setField(entity, field, lazyValue);
			} else {
				ReflectionUtil.setField(entity, field, null);
			}
		}

		return entity;
	}
}
