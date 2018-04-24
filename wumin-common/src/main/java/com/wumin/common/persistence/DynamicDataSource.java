package com.wumin.common.persistence;

import com.wumin.common.web.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源（数据源切换）
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	private final static Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSource = getDataSource();
		LOGGER.info("当前操作使用的数据源：{}", dataSource);
		return dataSource;
	}

	/**
	 * 设置数据源
	 * @param dataSource
	 */
	public static void setDataSource(String dataSource) {
		ThreadContext.setDataSource(dataSource);
	}

	/**
	 * 获取数据源
	 * @return
	 */
	public static String getDataSource() {
		String dataSource = ThreadContext.getDataSource();
		// 如果没有指定数据源，使用默认数据源
		if (null == dataSource) {
			setDataSource(DataSourceEnum.MASTER.getName());
		}
		return ThreadContext.getDataSource();
	}

}
