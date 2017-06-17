package com.maps.utils.validate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * 
 */
public class StringUtil {

	public static boolean existChinese(String s) {
		Pattern pattern = Pattern.compile("[\u4E00-\uFA29]");
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	public static boolean existDoubleByteCharacters(String s) {
		Pattern pattern = Pattern.compile("[^\\x00-\\xff]");
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (null == email || email.equals(""))
			return false;
		email = email.toLowerCase();
		if (email.endsWith(".con"))
			return false;
		if (email.endsWith(".cm"))
			return false;
		if (email.endsWith("@gmial.com"))
			return false;
		if (email.endsWith("@gamil.com"))
			return false;
		if (email.endsWith("@gmai.com"))
			return false;
		Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		return emailer.matcher(email).matches();
	}

	/**
	 * 单字节转换成16进制
	 * 
	 * @param byteValue
	 * @return
	 */
	public static String byteToHexString(byte byteValue) {
		final StringBuilder hexString = new StringBuilder();
		if ((byteValue & 0xff) < 0x10)// 0~F前面不零
			hexString.append("0");
		return hexString.append(Integer.toHexString(0xFF & byteValue)).toString().toLowerCase();
	}

	/**
	 * 字节数组转成16进制表示格式的字符串
	 * 
	 * @param byteArray
	 *            需要转换的字节数组
	 * @return 16进制表示格式的字符串
	 **/
	public static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException("this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)// 0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}

	/**
	 * 16进制的字符串表示转成字节数组
	 * 
	 * @param hexString
	 *            16进制格式的字符串
	 * @return 转换后的字节数组
	 **/
	public static byte[] toByteArray(String hexString) {
		if (null ==hexString || hexString.equals(""))
			throw new IllegalArgumentException("this hexString must not be empty");

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	public static void main(String[] agrs) {
		String s = "aaabbbcccsddsfsf";
		System.out.println(existChinese(s));
		System.out.println(buildMD5Value("administrator"));
	}

	/**
	 * 生成MD5验证这符串
	 * @param s
	 * @return
	 */
	public final static String buildMD5Value(String s) {
		System.out.println(s);
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes("utf-8");

			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 提取URL
	 * @param s
	 */
	public static String[] extractUrl(String s) {
		String HTTP_URL = "(([a-zA-Z0-9+-.]+://)*(([a-zA-Z0-9\\.\\-]+\\.(bb|so|com|cn|net|pro|org|int|info|xxx|biz|coop))|(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}))(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?(?=\\b|[^a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]))";
		String WWW_URL = "(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
		String REGEX_URL = HTTP_URL + "|" + WWW_URL;
		
//		String regex1 = "[^a-z](https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//		Pattern p = Pattern.compile(regex1);
		
		Pattern p = Pattern.compile(REGEX_URL);
		Matcher m = p.matcher(s);
		List<String> list = new ArrayList<String>();
		while (m.find()) {
			String str = m.group();
			list.add(str);
		}
		return list.size()>0?list.toArray(new String[0]):null;
	}
	
	/**
	 * 获取文件后缀
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName){
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}
	
	  public static  String parseToPath(String str) {
	        str = str.replace('\\', '/');
	        str = str.replaceAll("/{2,}","/");
	        return str;
	    }
	  
	  public static Integer string2Int(String str){
			if(str!=null && !str.equals("") && str.matches("[0-9].*")){
				if(str.contains(".")){
				  str  = str.substring(0,str.indexOf("."));
				}
				return Integer.parseInt(str);
			}
			return -1;
		}
	  
	  public static Integer null2Int(Integer str){
			 if(str==null){
				 return -1;
			 }
			return str;
		}
		
		public static String null2String(String str){
			if(str==null){
				return "";
			}
			return str;
		}
		
		public static String nullTwoString(String str){
			if(str==null||"null".equals(str)){
				return "";
			}
			return str;
		}
		
		public static String null2Default(String str,String defaultStr){
			if(str==null){
				return defaultStr;
			}
			return str;
		}
		
		public static String nullObj2String(Object obj){
			if(obj==null){
				return "";
			}
			return obj.toString();
		}
		
		public static Long nullObj2Long(Object obj){
			if(obj==null){
				return 0l;
			}
			return Long.valueOf(obj.toString());
		}
		
		public static Float string2float(String str){
			if(str!=null && !str.equals("")){
				return Float.valueOf(str);
			}
			return null;
		}
		
		public static InputStream StringTOInputStream(String in) throws Exception{  
	          
	        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));  
	        return is;  
	    }  
		
		

}
