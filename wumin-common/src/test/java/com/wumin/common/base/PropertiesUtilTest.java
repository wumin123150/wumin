package com.wumin.common.base;

import static org.assertj.core.api.Assertions.*;

import java.util.Properties;

import org.junit.Test;

public class PropertiesUtilTest {

	@Test
	public void loadProperties() {
		Properties p1 = PropertiesUtil.loadFromFile("classpath://application.properties");
		assertThat(p1.get("min")).isEqualTo("1");
		assertThat(p1.get("max")).isEqualTo("10");

		Properties p2 = PropertiesUtil.loadFromString("min=1\nmax=10\nisOpen=true");
		assertThat(PropertiesUtil.getInt(p2, "min", 0)).isEqualTo(1);
		assertThat(PropertiesUtil.getInt(p2, "max", 0)).isEqualTo(10);
		assertThat(PropertiesUtil.getInt(p2, "maxA", 0)).isEqualTo(0);

		assertThat(PropertiesUtil.getLong(p2, "min", 0L)).isEqualTo(1);
		assertThat(PropertiesUtil.getLong(p2, "max", 0L)).isEqualTo(10);
		assertThat(PropertiesUtil.getLong(p2, "maxA", 0L)).isEqualTo(0);
		
		assertThat(PropertiesUtil.getDouble(p2, "min", 0d)).isEqualTo(1);
		assertThat(PropertiesUtil.getDouble(p2, "max", 0d)).isEqualTo(10);
		assertThat(PropertiesUtil.getDouble(p2, "maxA", 0d)).isEqualTo(0);
		
		assertThat(PropertiesUtil.getString(p2, "min", "")).isEqualTo("1");
		assertThat(PropertiesUtil.getString(p2, "max", "")).isEqualTo("10");
		assertThat(PropertiesUtil.getString(p2, "maxA", "")).isEqualTo("");

		assertThat(PropertiesUtil.getBoolean(p2, "isOpen", false)).isTrue();
	}

}
