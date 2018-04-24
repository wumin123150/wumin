package com.wumin.common.web;

import com.wumin.common.concurrent.ThreadLocalContext;
import com.wumin.common.security.shiro.ShiroUser;

public class ThreadContext extends ThreadLocalContext {
	
	public static final String USER_ID_KEY = "_user_id_key";
	public static final String USER_NAME_KEY = "_user_name_key";
	public static final String IP_KEY = "_ip_key";
	public static final String URL_KEY = "_url_key";
	public static final String DATA_SOURCE_KEY = "_data_source_key";
	public static final String UPLOAD_PATH_KEY = "_upload_path_key";
	
	public static Long getUserId() {
		return (Long) get(USER_ID_KEY);
	}

	public static void setUserId(Long userId) {
		put(USER_ID_KEY, userId);
	}
	
	public static String getUserName() {
		return (String) get(USER_NAME_KEY);
	}

	public static void setUserName(String userName) {
		put(USER_NAME_KEY, userName);
	}
	
	public static String getIp() {
		return (String) get(IP_KEY);
	}

	public static void setIp(String ip) {
		put(IP_KEY, ip);
	}
	
	public static String getUrl() {
		return (String) get(URL_KEY);
	}

	public static void setUrl(String url) {
		put(URL_KEY, url);
	}

	public static String getDataSource() {
		return (String) get(DATA_SOURCE_KEY);
	}

	public static void setDataSource(String dataSource) {
		put(DATA_SOURCE_KEY, dataSource);
	}

	public static String getUploadPath() {
		return (String) get(UPLOAD_PATH_KEY);
	}

	public static void setUploadPath(String uploadPath) {
		put(UPLOAD_PATH_KEY, uploadPath);
	}

}
