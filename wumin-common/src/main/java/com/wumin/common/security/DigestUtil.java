package com.wumin.common.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.Validate;

import com.wumin.common.base.ExceptionUtil;
import com.wumin.common.text.EncodeUtil;

/**
 * 支持SHA-256/SHA-1/MD5消息摘要的工具类.
 * 
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 
 * @author walker
 */
public class DigestUtil {

	private static final String SHA1 = "SHA-1";
	private static final String MD5 = "MD5";
	private static final String SHA256 = "SHA-256";
	
	private static SecureRandom random = new SecureRandom();

	/**
	 * 对输入字符串进行md5散列.
	 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
	 */
	public static byte[] md5(byte[] input) {
		return digest(input, MD5, null, 1);
	}
	
	/**
	 * 对输入字符串进行sha1散列.
	 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
	 */
	public static byte[] sha1(byte[] input) {
		return digest(input, SHA1, null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, SHA1, salt, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA1, salt, iterations);
	}
	
	/**
	 * 对输入字符串进行sha256散列.
	 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
	 */
	public static byte[] sha256(byte[] input) {
		return digest(input, SHA256, null, 1);
	}

	public static byte[] sha256(byte[] input, byte[] salt) {
		return digest(input, SHA256, salt, 1);
	}

	public static byte[] sha256(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA256, salt, iterations);
	}
	
	/**
	 * 对输入字符串进行md5散列.
	 */
	public static String md5(String input) {
		return digest(input, MD5, null, 1);
	}
	
	/**
	 * 对输入字符串进行sha1散列.
	 */
	public static String sha1(String input) {
		return digest(input, SHA1, null, 1);
	}
	
	/**
	 * 对输入字符串进行sha256散列.
	 */
	public static String sha256(String input) {
		return digest(input, SHA256, null, 1);
    }
	
	/**
	 * 对字符串进行散列, 支持md5/sha1/sha256算法.
	 */
	private static String digest(String input, String algorithm, byte[] salt, int iterations) {
		byte[] digest = digest(StringUtils.getBytesUtf8(input), algorithm, salt, iterations);
        return EncodeUtil.encodeBase64(digest);
	}

	/**
	 * 对字符串进行散列, 支持md5/sha1/sha256算法.
	 */
	private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

	/**
	 * 生成随机的Byte[]作为salt.
	 * 
	 * @param numBytes byte数组的大小
	 */
	public static byte[] generateSalt(int numBytes) {
		Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * 对文件进行md5散列.
	 */
	public static byte[] md5(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 对文件进行sha1散列.
	 */
	public static byte[] sha1(InputStream input) throws IOException {
		return digest(input, SHA1);
	}
	
	/**
	 * 对文件进行sha256散列.
	 */
	public static byte[] sha256(InputStream input) throws IOException {
		return digest(input, SHA256);
	}

	/**
	 * 对文件进行散列, 支持md5/sha1/sha256算法.
	 */
	private static byte[] digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return messageDigest.digest();
		} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
	}
	
	/**
	 * 对输入字符串进行md5散列验证.
	 */
	public static boolean validMd5(byte[] expected, byte[] input) {
		byte[] digest = md5(input);
		return Arrays.equals(digest, expected);
    }
	
	/**
	 * 对输入字符串进行sha1散列验证.
	 */
	public static boolean validSha1(byte[] expected, byte[] input) {
		byte[] digest = sha1(input);
		return Arrays.equals(digest, expected);
    }
	
	public static boolean validSha1(byte[] expected, byte[] input, byte[] salt) {
		byte[] digest = sha1(input, salt, 1);
		return Arrays.equals(digest, expected);
	}

	public static boolean validSha1(byte[] expected, byte[] input, byte[] salt, int iterations) {
		byte[] digest = sha1(input, salt, iterations);
		return Arrays.equals(digest, expected);
	}
	
	/**
	 * 对输入字符串进行sha256散列验证.
	 */
	public static boolean validSha256(byte[] expected, byte[] input) {
		byte[] digest = sha256(input);
        return Arrays.equals(digest, expected);
    }
	
	public static boolean validSha256(byte[] expected, byte[] input, byte[] salt) {
		byte[] digest = sha256(input, salt, 1);
        return Arrays.equals(digest, expected);
	}

	public static boolean validSha256(byte[] expected, byte[] input, byte[] salt, int iterations) {
		byte[] digest = sha256(input, salt, iterations);
        return Arrays.equals(digest, expected);
	}
	
	/**
	 * 对输入字符串进行md5散列验证.
	 */
	public static boolean validMd5(String expected, String input) {
		String digest = md5(input);
        return digest.equals(expected);
    }
	
	/**
	 * 对输入字符串进行sha1散列验证.
	 */
	public static boolean validSha1(String expected, String input) {
		String digest = sha1(input);
        return digest.equals(expected);
    }
	
	/**
	 * 对输入字符串进行sha256散列验证.
	 */
	public static boolean validSha256(String expected, String input) {
		String digest = sha256(input);
        return digest.equals(expected);
    }
	
}
