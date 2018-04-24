package com.wumin.common.security;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;

import com.wumin.common.base.ExceptionUtil;
import com.wumin.common.text.EncodeUtil;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * 
 * @author walker
 */
public class SpecUtil {

    /**
     * 加密算法RSA
     */
    public static final String RSA = "RSA";
    
    /**
     * 签名算法
     */
    public static final String MD5_RSA = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "PublicKey";
    
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "PrivateKey";
    
    /**
     * 密钥大小
     */
    public static final int DEFAULT_RSA_KEYSIZE = 1024;
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Key> generateRsaKeyPair() {
        return generateRsaKey(DEFAULT_RSA_KEYSIZE);
    }
    
    /**
	 * 生成密钥对(公钥和私钥),可选长度为1024,2048位.
	 */
	public static Map<String, Key> generateRsaKey(int keysize) {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
	        keyPairGen.initialize(keysize);
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        Key publicKey = keyPair.getPublic();
	        Key privateKey = keyPair.getPrivate();
	        Map<String, Key> keyMap = new HashMap<String, Key>(2);
	        keyMap.put(PUBLIC_KEY, publicKey);
	        keyMap.put(PRIVATE_KEY, privateKey);
	        return keyMap;
		} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
	}
	
	/**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param privateKey 私钥
     * 
     * @return
     */
    public static byte[] sign(byte[] data, byte[] privateKey) {
    	try {
	        // 构造PKCS8EncodedKeySpec对象 
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
	        // KEY_ALGORITHM 指定的加密算法 
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
	        // 取私钥匙对象 
	        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
	        // 用私钥对信息生成数字签名 
	        Signature signature = Signature.getInstance(MD5_RSA);
	        signature.initSign(privateK);
	        signature.update(data);
	        return signature.sign();
    	} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
    }
    
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * 
     * @return
     */
    public static String sign(String data, String privateKey) {
        byte[] md5RsaResult = sign(StringUtils.getBytesUtf8(data), EncodeUtil.decodeBase64(privateKey));
        return EncodeUtil.encodeBase64UrlSafe(md5RsaResult);
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param sign 数字签名
     * @param publicKey 公钥
     * 
     * @return 校验成功返回true 失败返回false 
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] publicKey) {
    	try {
    		// 构造X509EncodedKeySpec对象 
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            // KEY_ALGORITHM 指定的加密算法 
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            // 取公钥匙对象 
            PublicKey publicK = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(MD5_RSA);
            signature.initVerify(publicK);
            signature.update(data);
            // 验证签名是否正常 
            return signature.verify(sign);
    	} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
    }
    
    /**
     * <p>
     * 校验数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param sign 数字签名(BASE64编码)
     * @param publicKey 公钥(BASE64编码)

     * 
     * @return 校验成功返回true 失败返回false 
     * 
     */
    public static boolean verify(String data, String sign, String publicKey) {
        return verify(StringUtils.getBytesUtf8(data), EncodeUtil.decodeBase64(sign), EncodeUtil.decodeBase64(publicKey));
    }
    
    private static byte[] decrypt(byte[] encryptedData, Key key, int blockSize) {
    	try {
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int inputLen = encryptedData.length;
	        int offSet = 0;
	        int i = 0;
	        // 对数据分段解密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet < blockSize) {
	            	blockSize = inputLen - offSet;
	            } 
	            IOUtils.write(cipher.doFinal(encryptedData, offSet, blockSize), out);
	            offSet = (++i) * blockSize;
	        }
	        
	        byte[] decryptedData = out.toByteArray();
	        out.close();
	        return decryptedData;
    	} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
    }
    
    /**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param privateKey 私钥
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, byte[] privateKey, int blockSize) {
        Key key = getPrivateKey(privateKey);
        return decrypt(encryptedData, key, blockSize);
    }
    
    /**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param privateKey 私钥
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, byte[] privateKey) {
        return decryptByPrivateKey(encryptedData, privateKey, MAX_DECRYPT_BLOCK);
    }
    
    /**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据(BASE64编码)
     * @param privateKey 私钥(BASE64编码)
     * @return
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey) {
        byte[] result = decryptByPrivateKey(EncodeUtil.decodeBase64(encryptedData), EncodeUtil.decodeBase64(privateKey), MAX_DECRYPT_BLOCK);
        return StringUtils.newStringUtf8(result);
    }
    
    /**
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param publicKey 公钥
     * @return
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, byte[] publicKey, int blockSize) {
        Key key = getPublicKey(publicKey);
        return decrypt(encryptedData, key, blockSize);
    }
    
    /**
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, byte[] publicKey) {
    	return decryptByPublicKey(encryptedData, publicKey, MAX_DECRYPT_BLOCK);
    }
    
    /**
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据(BASE64编码)
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String encryptedData, String publicKey) {
    	byte[] result = decryptByPublicKey(EncodeUtil.decodeBase64(encryptedData), EncodeUtil.decodeBase64(publicKey), MAX_DECRYPT_BLOCK);
    	return StringUtils.newStringUtf8(result);
    }
    
    private static byte[] encrypt(byte[] data, Key key, int blockSize) {
    	try {
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
	        // 对数据加密
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int inputLen = data.length;
	        int offSet = 0;
	        int i = 0;
	        int size = blockSize;
	        // 对数据分段加密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet < size) {
	            	size = inputLen - offSet;
	            } 
	            IOUtils.write(cipher.doFinal(data, offSet, size), out);
	            offSet = (++i) * size;
	        }
	        
	        byte[] encryptedData = out.toByteArray();
	        out.close();
	        return encryptedData;
    	} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
    }
    
    /**
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param privateKey 私钥
     * @return
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey, int blockSize) {
        Key key = getPrivateKey(privateKey);
        return encrypt(data, key, blockSize);
    }
    
    /**
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param privateKey 私钥
     * @return
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) {
        return encryptByPrivateKey(data, privateKey, MAX_ENCRYPT_BLOCK);
    }
    
    /**
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return (BASE64编码)
     */
    public static String encryptByPrivateKey(String data, String privateKey) {
        byte[] result = encryptByPrivateKey(StringUtils.getBytesUtf8(data), EncodeUtil.decodeBase64(privateKey), MAX_ENCRYPT_BLOCK);
        return EncodeUtil.encodeBase64UrlSafe(result);
    }
    
    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param publicKey 公钥
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey, int blockSize) {
	    Key key = getPublicKey(publicKey);
	    return encrypt(data, key, blockSize);
    }
    
    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param publicKey 公钥
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
    	return encryptByPublicKey(data, publicKey, MAX_ENCRYPT_BLOCK);
    }
    
    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return (BASE64编码)
     */
    public static String encryptByPublicKey(String data, String publicKey) {
    	byte[] result = encryptByPublicKey(StringUtils.getBytesUtf8(data), EncodeUtil.decodeBase64(publicKey), MAX_ENCRYPT_BLOCK);
        return EncodeUtil.encodeBase64UrlSafe(result);
    }
    
    /**
     * 获取私钥
     * 
     * @param keyMap 密钥对
     * @return
     */
    public static byte[] getPrivateKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }
    
    /**
     * 获取私钥
     * 
     * @param keyMap 密钥对
     * @return
     */
    public static String getPrivatePassword(Map<String, Key> keyMap) {
        return EncodeUtil.encodeBase64(getPrivateKey(keyMap));
    }
    
    /**
     * 获取私钥
     * 
     * @param privateKey 私钥
     * @return
     */
    private static Key getPrivateKey(byte[] privateKey) {
    	try {
	    	PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
	        return keyFactory.generatePrivate(pkcs8KeySpec);
    	} catch (GeneralSecurityException e) {
			throw ExceptionUtil.unchecked(e);
		}
    }

    /**
     * 获取公钥
     * 
     * @param keyMap 密钥对
     * @return
     */
    public static byte[] getPublicKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }
    
    /**
     * 获取公钥
     * 
     * @param keyMap 密钥对
     * @return
     */
    public static String getPublicPassword(Map<String, Key> keyMap) {
        return EncodeUtil.encodeBase64(getPublicKey(keyMap));
    }
    
    /**
     * 获取公钥
     * 
     * @param publicKey 公钥
     * @return
     */
    private static Key getPublicKey(byte[] publicKey) {
    	try {
	    	X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
	        return keyFactory.generatePublic(x509KeySpec);
    	} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
    }
    
//    public static void main(String[] args) throws Exception {
//    	try {
//            Map<String, Object> keyMap = Specs.generateRsaKeyPair();
//            String publicKey = Specs.getPublicPassword(keyMap);
//            String privateKey = Specs.getPrivatePassword(keyMap);
//            System.err.println("公钥: \n\r" + publicKey);
//            System.err.println("私钥： \n\r" + privateKey);
//            test(publicKey, privateKey);
//            testSign(publicKey, privateKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    static void test(String publicKey, String privateKey) throws Exception {
//        System.err.println("公钥加密——私钥解密");
//        String source = "062477";
//        System.out.println("\r加密前文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = Specs.encryptByPublicKey(data, publicKey);
//        System.out.println("加密后文字：" + new String(encodedData).length() + "\r\n" + new String(encodedData));
//        byte[] decodedData = Specs.decryptByPrivateKey(encodedData, privateKey);
//        String target = new String(decodedData);
//        System.out.println("解密后文字: \r\n" + target);
//    }
//
//    static void testSign(String publicKey, String privateKey) throws Exception {
//        System.err.println("私钥加密——公钥解密");
//        String source = "这是一行测试RSA数字签名的无意义文字";
//        System.out.println("原文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = Specs.encryptByPrivateKey(data, privateKey);
//        System.out.println("加密后：" + new String(encodedData).length() + "\r\n" + new String(encodedData));
//        byte[] decodedData = Specs.decryptByPublicKey(encodedData, publicKey);
//        String target = new String(decodedData);
//        System.out.println("解密后: \r\n" + target);
//        System.err.println("私钥签名——公钥验证签名");
//        String sign = Specs.sign(encodedData, privateKey);
//        System.err.println("签名:\r" + sign);
//        boolean status = Specs.verify(encodedData, publicKey, sign);
//        System.err.println("验证结果:\r" + status);
//    }

}
