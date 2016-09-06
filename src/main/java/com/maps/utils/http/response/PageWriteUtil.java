package com.maps.utils.http.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;


/**
 * 将jsonObj对象输出到响应工具类，经常使用与以下场景：
 * 1、提供的restAPI接口时，通常调用该方法以json格式返回
 * 2、当前台通过ajax的方式向后台提交数据时，后台通常以resposne方式返回响应信息
 */
public class PageWriteUtil {
	
	 public enum contentTypeEnum { 
		 xml("text/XML"),html("text/text;charset=UTF-8L"),json("text/JSON"); 
		 private String value; 
		 contentTypeEnum(String value){
		  this.value = value;
		 }
		 public String getType(){
			 return value;
		 }
	 } 
	
	 /**
	  * html方式响应
	  * @param response
	  * @param jsonObj
	  */
	public static void writeToPageByHtml(HttpServletResponse response,Object jsonObj){
		response.setCharacterEncoding("utf-8");
		response.setContentType(contentTypeEnum.html.getType());
		try {
			response.getWriter().print(jsonObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
	  * xml方式响应
	  * @param response
	  * @param jsonObj
	  */
	public static void writeToPageByXML(HttpServletResponse response,Object jsonObj){
		response.setCharacterEncoding("utf-8");
		response.setContentType(contentTypeEnum.xml.getType());
		try {
			response.getWriter().print(jsonObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
	  * json方式响应
	  * @param response
	  * @param jsonObj
	  */
	public static void writeToPageByJSON(HttpServletResponse response,Object jsonObj){
			response.setCharacterEncoding("utf-8");
			response.setContentType(contentTypeEnum.json.getType());
			
			try {
				response.getWriter().print(jsonObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
