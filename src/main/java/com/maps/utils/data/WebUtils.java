package com.xes.teacherrestserver.web.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class WebUtils {

	public static String generateID(){
		return UUID.randomUUID().toString();
	}
	
	
	public static <T> T request2Bean(HttpServletRequest request,Class<T> clazz){
		try{
			T bean = clazz.newInstance();
			Map map = request.getParameterMap();
			ConvertUtils.register(new DateLocaleConverter(), Date.class);
			BeanUtils.populate(bean, map);  
			return bean;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
