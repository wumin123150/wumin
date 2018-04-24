package com.wumin.common.net;

import com.wumin.common.mapper.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String TEXT_PLAIN = "text/plain";

	private static JsonMapper json;
	private static SSLContext sslcontext = null;
	private static SSLConnectionSocketFactory sslSocketFactory = null;
	private static PoolingHttpClientConnectionManager connectionManager = null;
//  private static RequestConfig requestConfig = null;

	static {
		json = JsonMapper.nonEmptyMapper();

		try {
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslSocketFactory = new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sslSocketFactory)
				.build();
			connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			// 配置超时时间（连接服务端超时1分，请求数据返回超时2分）
//          requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000).setConnectionRequestTimeout(60000).build();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("远程连接初始化失败：", e);
		}
	}

	public static HttpResponse getResponse(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		String uri = buildQuery(url, bodyParams, charset);
		LOGGER.info("api uri:" + uri);
		LOGGER.info("api head params:" + (headParams != null ? json.toJson(headParams) : ""));
		if(context != null && context.getCookieStore() != null) {
			LOGGER.debug("api cookies:" + json.toJson(context.getCookieStore().getCookies()));
		}

		HttpGet httpGet = new HttpGet(uri);
		try {
			HttpClient client = getPoolingHttpClient();
			if(headParams != null) {
				for (Entry<String, String> entry : headParams.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}

			HttpResponse response = client.execute(httpGet, context == null ? getContext() : context);
			return response;
		} catch (Exception e) {
			String msg = String.format("Failed to call api '%s'", url);
			LOGGER.error(msg, e);
			httpGet.abort();
			throw e;
		}
	}

	public static HttpEntity getEntity(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		HttpResponse response = getResponse(url, bodyParams, headParams, charset, context);
		int status = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (status != 200) {
			throw new RuntimeException(String.format("api return error http code %d, detail: %n%s", status, EntityUtils.toString(entity, charset)));
		}
		return entity;
	}

	public static String getString(String url) throws Exception {
		return EntityUtils.toString(getEntity(url, null, null, "UTF-8", getContext()), "UTF-8");
	}

	public static String getString(String url, String charset) throws Exception {
		return EntityUtils.toString(getEntity(url, null, null, charset, getContext()), charset);
	}

	public static String getString(String url, Map<String, String> params) throws Exception {
		return EntityUtils.toString(getEntity(url, params, null, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String getString(String url, Map<String, String> params, String charset) throws Exception {
		return EntityUtils.toString(getEntity(url, params, null, charset, getContext()), charset);
	}

	public static String getString(String url, Map<String, String> bodyParams, Map<String, String> headParams) throws Exception {
		return EntityUtils.toString(getEntity(url, bodyParams, headParams, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String getString(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset) throws Exception {
		return EntityUtils.toString(getEntity(url, bodyParams, headParams, charset, getContext()), charset);
	}

	public static String getString(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		return EntityUtils.toString(getEntity(url, bodyParams, headParams, charset, context), charset);
	}

	public static HttpResponse putResponse(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		LOGGER.info("api url:" + url);
		LOGGER.info("api head params:" + (headParams != null ? json.toJson(headParams) : ""));
		LOGGER.info("api body params:" + (bodyParams != null ? json.toJson(bodyParams) : ""));
		if(context != null && context.getCookieStore() != null) {
			LOGGER.info("api cookies:" + json.toJson(context.getCookieStore().getCookies()));
		}

		HttpPut httpPut = new HttpPut(url);
		try {
			HttpClient client = getPoolingHttpClient();
			if(headParams != null) {
				for (Entry<String, String> entry : headParams.entrySet()) {
					httpPut.addHeader(entry.getKey(), entry.getValue());
				}
			}
			if(bodyParams != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : bodyParams.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPut.setEntity(new UrlEncodedFormEntity(nvps, charset));
			}

			HttpResponse response = client.execute(httpPut, context == null ? getContext() : context);
			return response;
		} catch (Exception e) {
			String msg = String.format("Failed to call api '%s'", url);
			LOGGER.error(msg, e);
			httpPut.abort();
			throw e;
		}
	}

	public static HttpEntity putEntity(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		HttpResponse response = putResponse(url, bodyParams, headParams, charset, context);
		int status = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (status != 200) {
			throw new RuntimeException(String.format("api return error http code %d, detail: %n%s", status, EntityUtils.toString(entity, charset)));
		}
		return entity;
	}

	public static String putString(String url) throws Exception {
		return EntityUtils.toString(putEntity(url, null, null, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String putString(String url, String charset) throws Exception {
		return EntityUtils.toString(putEntity(url, null, null, charset, getContext()), charset);
	}

	public static String putString(String url, Map<String, String> params) throws Exception {
		return EntityUtils.toString(putEntity(url, params, null, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String putString(String url, Map<String, String> params, String charset) throws Exception {
		return EntityUtils.toString(putEntity(url, params, null, charset, getContext()), charset);
	}

	public static String putString(String url, Map<String, String> bodyParams, Map<String, String> headParams) throws Exception {
		return EntityUtils.toString(putEntity(url, bodyParams, headParams, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String putString(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset) throws Exception {
		return EntityUtils.toString(putEntity(url, bodyParams, headParams, charset, getContext()), charset);
	}

	public static String putString(String url, Map<String, String> bodyParams, Map<String, String> headParams, String charset, HttpClientContext context) throws Exception {
		return EntityUtils.toString(putEntity(url, bodyParams, headParams, charset, context), charset);
	}

	public static HttpResponse postResponse(String url, Map<String, Object> bodyParams, Map<String, String> headParams, String mimeType, String charset, HttpClientContext context) throws Exception {
		LOGGER.info("api url:" + url);
		LOGGER.info("api head params:" + (headParams != null ? json.toJson(headParams) : ""));
		LOGGER.info("api body params:" + (bodyParams != null ? json.toJson(bodyParams) : ""));
		if(context != null && context.getCookieStore() != null) {
			LOGGER.info("api cookies:" + json.toJson(context.getCookieStore().getCookies()));
		}

    HttpPost httpPost = new HttpPost(url);
    try {
      HttpClient client = getPoolingHttpClient();
      if(headParams != null) {
        for (Entry<String, String> entry : headParams.entrySet()) {
          httpPost.addHeader(entry.getKey(), entry.getValue());
        }
      }
      if(bodyParams != null) {
        if(APPLICATION_JSON.equals(mimeType)) {
          StringEntity entity = new StringEntity(json.toJson(bodyParams), ContentType.create(mimeType, charset));
          entity.setContentEncoding(charset);
          httpPost.setEntity(entity);
        } else if(APPLICATION_XML.equals(mimeType)) {
          //TODO:
        } else if(MULTIPART_FORM_DATA.equals(mimeType)) {
          MultipartEntityBuilder builder = MultipartEntityBuilder.create();
          for (Entry<String, ? extends Object> entry : bodyParams.entrySet()) {
            if(entry.getValue() instanceof File) {
              FileBody fileBody = new FileBody((File)entry.getValue());
              builder.addPart(entry.getKey(), fileBody);
            } else {
              StringBody stringBody = new StringBody(entry.getValue() != null ? entry.getValue().toString() : "", ContentType.create(TEXT_PLAIN, charset));
              builder.addPart(entry.getKey(), stringBody);
            }
          }
          HttpEntity entity = builder.build();
          httpPost.setEntity(entity);
        } else {
          List<NameValuePair> nvps = new ArrayList<NameValuePair>();
          for (Entry<String, ? extends Object> entry : bodyParams.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : ""));
          }
          UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, charset);
          entity.setContentEncoding(charset);
          httpPost.setEntity(entity);
        }
      }

      HttpResponse response = client.execute(httpPost, context == null ? getContext() : context);
      return response;
    } catch (Exception e) {
      String msg = String.format("Failed to call api '%s'", url);
      LOGGER.error(msg, e);
      httpPost.abort();
      throw e;
    }
	}

	public static HttpEntity postEntity(String url, Map<String, Object> bodyParams, Map<String, String> headParams, String mimeType, String charset, HttpClientContext context) throws Exception {
		HttpResponse response = postResponse(url, bodyParams, headParams, mimeType, charset, context);
		int status = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (status != 200) {
			throw new RuntimeException(String.format("api return error http code %d, detail: %n%s", status, EntityUtils.toString(entity, charset)));
		}
		return entity;
	}

	public static String postString(String url) throws Exception {
		return EntityUtils.toString(postEntity(url, null, null, APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String postString(String url, String charset) throws Exception {
		return EntityUtils.toString(postEntity(url, null, null, APPLICATION_FORM_URLENCODED, charset, getContext()), charset);
	}

	public static String postString(String url, Map<String, Object> params) throws Exception {
		return EntityUtils.toString(postEntity(url, params, null, APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String postString(String url, Map<String, Object> params, String mimeType) throws Exception {
		return EntityUtils.toString(postEntity(url, params, null, mimeType, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String postString(String url, Map<String, Object> params, String mimeType, String charset) throws Exception {
		return EntityUtils.toString(postEntity(url, params, null, mimeType, charset, getContext()), charset);
	}

	public static String postString(String url, Map<String, Object> bodyParams, Map<String, String> headParams) throws Exception {
		return EntityUtils.toString(postEntity(url, bodyParams, headParams, APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String postString(String url, Map<String, Object> bodyParams, Map<String, String> headParams, String mimeType) throws Exception {
		return EntityUtils.toString(postEntity(url, bodyParams, headParams, mimeType, DEFAULT_CHARSET, getContext()), DEFAULT_CHARSET);
	}

	public static String postString(String url, Map<String, Object> bodyParams, Map<String, String> headParams, String mimeType, String charset) throws Exception {
		return EntityUtils.toString(postEntity(url, bodyParams, headParams, mimeType, charset, getContext()), charset);
	}

	public static String postString(String url, Map<String, Object> bodyParams, Map<String, String> headParams, String mimeType, String charset, HttpClientContext context) throws Exception {
		return EntityUtils.toString(postEntity(url, bodyParams, headParams, mimeType, charset, context), charset);
	}

	/**
	 *
	 * @param params 请求参数
	 * @return 构建query
	 */
	public static String buildQuery(String url, Map<String, String> params, String charset) {
		if (params == null || params.isEmpty()) {
			return url;
		}

		String cs = charset != null ? charset : DEFAULT_CHARSET;
		StringBuilder sb = new StringBuilder(url);
		if(StringUtils.contains(url, "?")) {
			sb.append("&");
		} else {
			sb.append("?");
		}

		boolean first = true;
		for (Entry<String, String> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
				try {
					sb.append(key).append("=").append(URLEncoder.encode(value, cs));
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		return sb.toString();

	}

	public static CloseableHttpClient getPoolingHttpClient() {
		return HttpClientBuilder.create()
			.setConnectionManager(connectionManager)
			.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
			.setRedirectStrategy(new DefaultRedirectStrategy())
//              .setDefaultRequestConfig(requestConfig)
//              .setDefaultCookieStore(new BasicCookieStore())
			.build();
	}

	public static CloseableHttpClient getHttpClient() {
		return HttpClientBuilder.create()
			.setSSLSocketFactory(sslSocketFactory)
			.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
			.setRedirectStrategy(new DefaultRedirectStrategy())
//              .setDefaultRequestConfig(requestConfig)
//              .setDefaultCookieStore(new BasicCookieStore())
			.build();
	}

	public static HttpClientContext getContext() {
		return HttpClientContext.create();
	}

	public static List<Cookie> getCookies(HttpClientContext context) {
		return context.getCookieStore().getCookies();
	}

	public static String getCookie(HttpClientContext context, String name) {
		List<Cookie> cookies = context.getCookieStore().getCookies();
		for (Cookie cookie : cookies) {
			if (StringUtils.equals(name, cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}

}
