package com.wumin.common.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.wumin.common.number.NumberUtil;
import com.wumin.common.text.MoreStringUtil;
import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * InetAddress工具类，基于Guava的InetAddresses.
 * 
 * 主要包含int, String/IPV4String, InetAdress/Inet4Address之间的互相转换
 * 
 * 先将字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
 * 
 * InetAddress与String的转换其实消耗不小，如果是有限的地址，建议进行缓存.
 * 
 * @author walker
 */
public class IPUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(IPUtil.class);

	/**
	 * 获取客户端IP地址.
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = null;
		ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				try {
					InetAddress inet = InetAddress.getLocalHost();
					ip = inet.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					LOGGER.error("域名解析失败：", e);
				}
			}
		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ip != null && ip.length() > 15) { // "***.***.***.***".length() = 15
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

	/**
	 * 从InetAddress转化到int, 传输和存储时, 用int代表InetAddress是最小的开销.
	 * 
	 * InetAddress可以是IPV4或IPV6，都会转成IPV4.
	 * 
	 * @see com.google.common.net.InetAddresses#coerceToInteger(InetAddress)
	 */
	public static int toInt(InetAddress address) {
		return InetAddresses.coerceToInteger(address);
	}

	/**
	 * InetAddress转换为String.
	 * 
	 * InetAddress可以是IPV4或IPV6. 其中IPV4直接调用getHostAddress()
	 * 
	 * @see com.google.common.net.InetAddresses#toAddrString(InetAddress)
	 */
	public static String toString(InetAddress address) {
		return InetAddresses.toAddrString(address);
	}

	/**
	 * 从int转换为Inet4Address(仅支持IPV4)
	 */
	public static Inet4Address fromInt(int address) {
		return InetAddresses.fromInteger(address);
	}

	/**
	 * 从String转换为InetAddress.
	 * 
	 * IpString可以是ipv4 或 ipv6 string, 但不可以是域名.
	 * 
	 * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
	 */
	public static InetAddress fromIpString(String address) {
		return InetAddresses.forString(address);
	}

	/**
	 * 从IPv4String转换为InetAddress.
	 * 
	 * IpString如果确定ipv4, 使用本方法减少字符分析消耗 .
	 * 
	 * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
	 */
	public static Inet4Address fromIpv4String(String address) {
		byte[] bytes = ip4StringToBytes(address);
		if (bytes == null) {
			return null;
		} else {
			try {
				return (Inet4Address) Inet4Address.getByAddress(bytes);
			} catch (UnknownHostException e) {
				throw new AssertionError(e);
			}
		}
	}

	/**
	 * int转换到IPV4 String, from Netty NetUtil
	 */
	public static String intToIpv4String(int i) {
		return new StringBuilder(15).append(i >> 24 & 0xff).append('.').append(i >> 16 & 0xff).append('.')
				.append(i >> 8 & 0xff).append('.').append(i & 0xff).toString();
	}

	/**
	 * Ipv4 String 转换到int
	 */
	public static int ipv4StringToInt(String ipv4Str) {
		byte[] byteAddress = ip4StringToBytes(ipv4Str);
		if (byteAddress == null) {
			return 0;
		} else {
			return NumberUtil.toInt(byteAddress);
		}
	}

	/**
	 * Ipv4 String 转换到byte[]
	 */
	private static byte[] ip4StringToBytes(String ipv4Str) {
		if (ipv4Str == null) {
			return null;
		}

		List<String> it = MoreStringUtil.split(ipv4Str, '.', 4);
		if (it.size() != 4) {
			return null;
		}

		byte[] byteAddress = new byte[4];
		for (int i = 0; i < 4; i++) {
			int tempInt = Integer.parseInt(it.get(i));
			if (tempInt > 255) {
				return null;
			}
			byteAddress[i] = (byte) tempInt;
		}
		return byteAddress;
	}
}
