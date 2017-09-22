package com.xes.teacherrestserver.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * @author 弓永杰【gongyongjie@100tal.com】
 *
 */
public class RegExpUtil {

	/**
	 * 验证手机号格式
	 * @param phone
	 * @return
	 */
	public static boolean isThePhoneNum(String phone){
		
		String regExp = "^1[0-9]{10}$";
		Pattern p = Pattern.compile(regExp);  
		Matcher m = p.matcher(phone);
		return m.find();
	}
}
