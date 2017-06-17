package com.maps.utils.data;

import java.util.Locale;

/*import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;*/

/**
 * Spring上下文的工具类
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 * @author ying
 */
/*@Component*/
public class SpringContextUtils /*implements ApplicationContextAware*/ {/*
	private static ApplicationContext applicationContext;

	*//**
	 * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
	 *//*
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtils.applicationContext = applicationContext; // NOSONAR
	}

	*//**
	 * 取得存储在静态变量中的ApplicationContext.
	 *//*
	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return applicationContext;
	}

	*//**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 *//*
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	*//**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 *//*
	public static <T> T getBean(Class<T> clazz) {
		checkApplicationContext();
		return applicationContext.getBean(clazz);
	}
	
	*//**
	 * 从Spring上下文中根据资源code取得消息资源
	 * @param code
	 * @return
	 *//*
	public static String getMessage(String code){
		checkApplicationContext();
		return applicationContext.getMessage(code, null, Locale.CHINESE);
	}
	
	*//**
	 * 从Spring上下文中根据资源code取得消息资源
	 * @param code
	 * @param args 占位符(如：{0})参数
	 * @return
	 *//*
	public static String getMessage(String code, Object[] args){
		checkApplicationContext();
		return applicationContext.getMessage(code, args, Locale.CHINESE);
	}
	
	*//**
	 * 从Spring上下文中根据资源code取得消息资源
	 * @param code
	 * @param args 占位符(如：{0})参数
	 * @param locale 
	 * @return
	 *//*
	public static String getMessage(String code, Object[] args, Locale locale){
		checkApplicationContext();
		return applicationContext.getMessage(code, args, locale);
	}
	
	*//**
	 * 从Spring上下文中根据资源code取得消息资源
	 * @param code 
	 * @param args 占位符(如：{0})参数
	 * @param defaultMessage 如果根据code找不到消息，默认返回的消息
	 * @param locale
	 * @return
	 *//*
	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale){
		checkApplicationContext();
		return applicationContext.getMessage(code, args, defaultMessage, locale);
	}

	*//**
	 * 清除applicationContext静态变量.
	 *//*
	public static void cleanApplicationContext() {
		applicationContext = null;
	}

	private static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException(
					"applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}
*/}