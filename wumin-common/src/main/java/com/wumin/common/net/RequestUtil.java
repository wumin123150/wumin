package com.wumin.common.net;

import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.wumin.common.collection.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import com.wumin.common.collection.ArrayUtil;
import com.wumin.common.collection.MapUtil;

/**
 * HttpServletRequest帮助类
 */
public class RequestUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);

  /**
   * HTTP POST请求
   */
  public static final String POST = "POST";

  /**
   * HTTP GET请求
   */
  public static final String GET = "GET";

  /**
   * cookie中的JSESSIONID名称
   */
  public static final String JSESSION_COOKIE = "JSESSIONID";

  public static String getHeader(HttpServletRequest request, String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    return request.getHeader(name);
  }

  public static Map<String, Object> getHeaders(HttpServletRequest request) {
    Enumeration headerNames = request.getHeaderNames();
    Map<String, Object> headers = new HashMap<String, Object>();
    while (headerNames.hasMoreElements()) {
      String headerName = (String) headerNames.nextElement();
      request.getHeaders(headerName);
      List<String> values = Collections.list(request.getHeaders(headerName));
      if(!values.isEmpty()) {
        if(values.size() == 1) {
          headers.put(headerName, values.get(0));
        } else {
          headers.put(headerName, values.toArray(new String[values.size()]));
        }
      }
    }
    return headers;
  }

  public static Map<String, Object> getParameters(HttpServletRequest request) {
    Enumeration paramNames = request.getParameterNames();
    Map<String, Object> params = new HashMap<String, Object>();
    while (paramNames != null && paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      String[] values = request.getParameterValues(paramName);
      if (values == null || values.length == 0) {
        // Do nothing, no values found at all.
      } else if (values.length > 1) {
        params.put(paramName, values);
      } else {
        params.put(paramName, values[0]);
      }
    }
    return params;
  }

  public static Map<String, Object> getParameters(HttpServletRequest request, List<String> excludes) {
    Enumeration paramNames = request.getParameterNames();
    Map<String, Object> params = new HashMap<String, Object>();
    while (paramNames != null && paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      if (!excludes.contains(paramName)) {
        String[] values = request.getParameterValues(paramName);
        if (values == null || values.length == 0) {
          // Do nothing, no values found at all.
        } else if (values.length > 1) {
          params.put(paramName, values);
        } else {
          params.put(paramName, values[0]);
        }
      }
    }
    return params;
  }

  /**
   * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
   * <p>
   * 返回的结果的Parameter名已去除前缀.
   */
  public static Map<String, Object> getParametersWithPrefix(HttpServletRequest request, String prefix) {
    if (prefix == null) {
      prefix = "";
    }

    Enumeration paramNames = request.getParameterNames();
    Map<String, Object> params = new HashMap<String, Object>();
    while (paramNames != null && paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      if ("".equals(prefix) || paramName.startsWith(prefix)) {
        String unprefixed = paramName.substring(prefix.length());
        String[] values = request.getParameterValues(paramName);
        if (values == null || values.length == 0) {
          // Do nothing, no values found at all.
        } else if (values.length > 1) {
          params.put(unprefixed, values);
        } else {
          params.put(unprefixed, values[0]);
        }
      }
    }
    return params;
  }

  public static Map<String, Object> parseQueryString(String s) {
    Map<String, Object> params = new HashMap<String, Object>();
    String[] pairs = StringUtils.split(s, "&");
    if (pairs != null) {
      String[] values;
      for (int i = 0; i < pairs.length; i++) {
        if (StringUtils.contains(pairs[i], "=")) {
          String key = StringUtils.substringBefore(pairs[i], "=");
          String value = StringUtils.substringAfter(pairs[i], "=");
          if (params.containsKey(key)) {
            Object oldValue = params.get(key);
            if (oldValue instanceof String) {
              values = new String[]{(String) oldValue, value};
              params.put(key, values);
            } else {
              values = ArrayUtil.concat((String[]) oldValue, value);
              params.put(key, values);
            }
          } else {
            params.put(key, value);
          }
        }
      }
    }
    return params;
  }

  /**
   * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
   *
   * @see #getParametersWithPrefix
   */
  public static String encodeParametersWithPrefix(Map<String, Object> params, String prefix) {
    if (MapUtil.isEmpty(params)) {
      return "";
    }

    if (prefix == null) {
      prefix = "";
    }

    StringBuilder queryStringBuilder = new StringBuilder();
    Iterator<Entry<String, Object>> it = params.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, Object> entry = it.next();
      queryStringBuilder.append(prefix).append(entry.getKey()).append('=').append(entry.getValue());
      if (it.hasNext()) {
        queryStringBuilder.append('&');
      }
    }
    return queryStringBuilder.toString();
  }

  /**
   * 获得当前的访问路径
   * <p>
   * HttpServletRequest.getRequestURL+"?"+HttpServletRequest.getQueryString
   *
   * @param request
   * @return
   */
  public static String getLocation(HttpServletRequest request) {
    UrlPathHelper helper = new UrlPathHelper();
    StringBuffer buff = request.getRequestURL();
    String uri = request.getRequestURI();
    String origUri = helper.getOriginatingRequestUri(request);
    buff.replace(buff.length() - uri.length(), buff.length(), origUri);
    String queryString = helper.getOriginatingQueryString(request);
    if (queryString != null) {
      buff.append("?").append(queryString);
    }
    return buff.toString();
  }

  public static String getLocation(HttpServletRequest request, String location) {
    StringBuffer buff = request.getRequestURL();
    String uri = request.getRequestURI();
    String path = request.getContextPath();
    buff.delete(buff.length() - uri.length(), buff.length()).append(path).append(location);
    return buff.toString();
  }

  public static String getLocalLocation(HttpServletRequest request) {
    UrlPathHelper helper = new UrlPathHelper();
    StringBuffer buff = new StringBuffer(helper.getOriginatingServletPath(request));
    String queryString = helper.getOriginatingQueryString(request);
    if (queryString != null) {
      buff.append("?").append(queryString);
    }
    return buff.toString();
  }

  /**
   * 获得请求的session id，但是HttpServletRequest#getRequestedSessionId()方法有一些问题。
   * 当存在部署路径的时候，会获取到根路径下的jsessionid。
   *
   * @param request
   * @return
   * @see HttpServletRequest#getRequestedSessionId()
   */
  public static String getRequestedSessionId(HttpServletRequest request) {
    String sid = request.getRequestedSessionId();
    String ctx = request.getContextPath();
    // 如果session id是从url中获取，或者部署路径为空，那么是在正确的。
    if (request.isRequestedSessionIdFromURL() || StringUtils.isBlank(ctx)) {
      return sid;
    } else {
      // 手动从cookie获取
      Cookie cookie = CookieUtil.getCookie(request, JSESSION_COOKIE);
      if (cookie != null) {
        return cookie.getValue();
      } else {
        return request.getSession().getId();
      }
    }
  }

  public static boolean isAjaxRequest(HttpServletRequest request) {
    return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
      || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
  }

  public static void main(String[] args) {

  }
}
