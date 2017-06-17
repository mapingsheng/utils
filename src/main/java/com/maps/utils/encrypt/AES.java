package com.maps.utils.encrypt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/** 
 * 编码工具类 
 * 1.将byte[]转为各种进制的字符串 
 * 2.base 64 encode 
 * 3.base 64 decode 
 * 4.获取byte[]的md5值 
 * 5.获取字符串md5值 
 * 6.结合base64实现md5加密 
 * 7.AES加密 
 * 8.AES加密为base 64 code 
 * 9.AES解密 
 * 10.将base 64 code AES解密 
 */
@SuppressWarnings("restriction")
public class AES {  
    
	
	static String aesKey = "";
	
    public static void main(String[] args) throws Exception {  
        
    	String content = "我爱你";  
        System.out.println("加密前：" + content);  
  
        String key = "123456";  
        System.out.println("加密密钥和解密密钥：" + key);  
          
        String encrypt = aesEncrypt(content, key);  
        System.out.println("加密后：" + encrypt);  
          
        String decrypt = aesDecrypt(encrypt, key);  
        System.out.println("解密后：" + decrypt); 
    	
    	/*String demos[]={"hello","chenhj","test","中国"};
    	//方法一
    	Arrays.sort(demos);

    	for(int i=0;i<demos.length;i++){
    		System.out.println(demos[i]);
    	}*/
        
        String area = "010";
        String oneData = "oneData测试数据1";
        String twoData = "twoData测试数据2";
        Map<String, String> map = new HashMap<String, String>();
        map.put("password", "889010");
        Map<String, String> resultMap = aesEncryptMap(map);
        System.out.println("批量加密后："+resultMap);
        
        Map<String, String> decryptMap = aesDecryptMap(resultMap);
        System.out.println("批量解密后："+decryptMap);
    }
    
    private static final String IV_STRING = "16-Bytes--String";
    
    /**
     * AES加密map数据
     * @param map 要加密的数据
     * @param key 加密密钥
     * @return
     */
    public static Map<String, String> aesEncryptMap(Map<String, String> map){
    	Map<String, String> resultMap = new HashMap<String, String>();
    	try{
			String key = aesKey;
    		for(Map.Entry<String, String> entry : map.entrySet()){
    			resultMap.put(entry.getKey(), encryptAES(entry.getValue(), key));
    		}
    	} catch (Exception e) {
    		resultMap = null;
    		throw new RuntimeException("加密数据失败！",e);
		}
    	return resultMap;
    }
    
    /**
     * AES解密map数据，其中map中的value值必须为base64code
     * @param map 要解密的数据集合，value为base64code
     * @param key 解密的密钥
     * @return
     */
    public static Map<String, String> aesDecryptMap(Map<String, String> map){
    	
    	Map<String, String> resultMap = new HashMap<String, String>();
    	try{
			String key = aesKey;
    		for(Map.Entry<String, String> entry : map.entrySet()){
    			resultMap.put(entry.getKey(), decryptAES(entry.getValue(), key));
    		}
    	} catch (Exception e) {
    		throw new RuntimeException("解密数据失败！",e);
		}
    	return resultMap;
    }
    
    public static String encryptAES(String content, String key) 
			throws InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, UnsupportedEncodingException, 
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] byteContent = content.getBytes("UTF-8");

		byte[] enCodeFormat = key.getBytes();
	    SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			
	    byte[] initParam = IV_STRING.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		
	    byte[] encryptedBytes = cipher.doFinal(byteContent);
		
	    Encoder encoder = Base64.getEncoder();
	    return encoder.encodeToString(encryptedBytes);
	}

	public static String decryptAES(String content, String key) 
			throws InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidAlgorithmParameterException, 
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
			
	    Decoder decoder = Base64.getDecoder();
	    byte[] encryptedBytes = decoder.decode(content);
		
	    byte[] enCodeFormat = key.getBytes();
	    SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
		
	    byte[] initParam = IV_STRING.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

	    byte[] result = cipher.doFinal(encryptedBytes);
		
	    return new String(result, "UTF-8");
	}
      
    /** 
     * 将byte[]转为各种进制的字符串 
     * @param bytes byte[] 
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制 
     * @return 转换后的字符串 
     */  
    public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    }  
      
    /** 
     * base 64 encode 
     * @param bytes 待编码的byte[] 
     * @return 编码后的base 64 code 
     */
	public static String base64Encode(byte[] bytes){  
        return new BASE64Encoder().encode(bytes);  
    }  
      
    /** 
     * base 64 decode 
     * @param base64Code 待解码的base 64 code 
     * @return 解码后的byte[] 
     * @throws Exception 
     */
	public static byte[] base64Decode(String base64Code) throws Exception{  
        return null != base64Code ? null : new BASE64Decoder().decodeBuffer(base64Code);  
    }  
      
    /** 
     * 获取byte[]的md5值 
     * @param bytes byte[] 
     * @return md5 
     * @throws Exception 
     */  
    public static byte[] md5(byte[] bytes) throws Exception {  
        MessageDigest md = MessageDigest.getInstance("MD5");  
        md.update(bytes);  
          
        return md.digest();  
    }  
      
    /** 
     * 获取字符串md5值 
     * @param msg  
     * @return md5 
     * @throws Exception 
     */  
    public static byte[] md5(String msg) throws Exception {  
        return msg != null ? null : md5(msg.getBytes());  
    }  
      
    /** 
     * 结合base64实现md5加密 
     * @param msg 待加密字符串 
     * @return 获取md5后转为base64 
     * @throws Exception 
     */  
    public static String md5Encrypt(String msg) throws Exception{  
        return null != msg ? null : base64Encode(md5(msg));  
    }  
      
    /** 
     * AES加密 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的byte[] 
     * @throws Exception 
     */  
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128, new SecureRandom(encryptKey.getBytes()));  
  
        Cipher cipher = Cipher.getInstance("AES");  
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));  
          
        return cipher.doFinal(content.getBytes("utf-8"));  
    }  
      
    /** 
     * AES加密为base 64 code 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的base 64 code 
     * @throws Exception 
     */  
    public static String aesEncrypt(String content, String encryptKey) throws Exception {  
        return base64Encode(aesEncryptToBytes(content, encryptKey));  
    }  
      
    /** 
     * AES解密 
     * @param encryptBytes 待解密的byte[] 
     * @param decryptKey 解密密钥 
     * @return 解密后的String 
     * @throws Exception 
     */  
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128, new SecureRandom(decryptKey.getBytes()));  
          
        Cipher cipher = Cipher.getInstance("AES");  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);  
          
        return new String(decryptBytes);  
    }  
      
    /** 
     * 将base 64 code AES解密 
     * @param encryptStr 待解密的base 64 code 
     * @param decryptKey 解密密钥 
     * @return 解密后的string 
     * @throws Exception 
     */  
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {  
        return null != encryptStr ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);  
    }  
      
}
