package com.maps.utils.validate;

import java.util.List;
import java.util.Map;

/**
 * 判断对应的数据是否为空(null、"")
 * @author 马平升
 *
 */
public class EmptyUtil {

	
	public static boolean isEmpty(Integer inter){
		if(null == inter){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEmpty(String str){
		if(null==str || "".equals(str.trim())){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEmpty(Object object){
		if(null == object){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEmpty(Map map){
		if(null == map || map.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEmpty(List list){
		if(null == list || list.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEmpty(Object[] objectArray){
		if(null == objectArray || objectArray.length==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断输入的多个字符串是否为空
	 * @param str 多个字符串参数
	 * @return
	 */
	public static boolean isEmpty(String...str){
		if(null == str) return true;
		for(int i=0; i<str.length; i++){
			if(null == str[i] || "".equals(str[i].trim())){
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		String[] sd = {"ss"};
		System.out.println(EmptyUtil.isEmpty(sd));

	}

}
