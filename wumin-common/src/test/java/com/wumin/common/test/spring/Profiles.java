package com.wumin.common.test.spring;

/**
 * Spring profile 常用方法与profile名称。
 * 
 * @author walker
 */
public class Profiles {

	public static final String ACTIVE_PROFILE = "spring.profiles.active";
	public static final String DEFAULT_PROFILE = "spring.profiles.default";

	public static final String PRODUCTION = "prod";
	public static final String DEVELOPMENT = "dev";
	public static final String UNIT_TEST = "test";
	public static final String FUNCTIONAL_TEST = "functional";

	/**
	 * 在Spring启动前，设置profile的环境变量。
	 */
	public static void setProfileAsSystemProperty(String profile) {
		System.setProperty(ACTIVE_PROFILE, profile);
	}
}
