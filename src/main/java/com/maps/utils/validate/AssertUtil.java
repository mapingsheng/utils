package com.maps.utils.validate;


/**
 * 
 *常用断言处理
 *
 * @version 
 *
 * @since 2016年10月14日
 */
public abstract class AssertUtil {

	/**
	 * 构造方法
	 */
	private AssertUtil() {
	}

	/**
	 * 字符串不能等于null或空，否则抛出异常
	 * @param obj 被判断的字符串
	 * @param objName 字符串变量的名称
	 */
	public static void notEmpty(String obj, String msg) {
		if (obj == null || obj.trim().equals("")) {
			throw new ServiceLayerException(msg);
		}else{
			msg = "";
		}
	}

	/**
	 * 对象不能为null，否则抛出异常
	 * @param obj 被判断的对象
	 * @param objName 对象的名字
	 */
	public static void notNull(Object obj, String msg) {
		if (obj == null) {
			throw new ServiceLayerException(msg);
		}else{
			msg = "";
		}
	}

	/**
	 * 对象不存在
	 * @param obj
	 */
	public static void notExist(String obj) {
		if (obj == null || obj.trim().equals("")) {
			throw new ServiceLayerException("操作失败");
		}
	}

	/**
	 * 对象不存在
	 * @param obj
	 */
	public static void notExist(String[] obj) {
		if (obj == null || obj.length == 0) {
			throw new ServiceLayerException("操作失败");
		}
	}
}

class ServiceLayerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceLayerException(String msg) {
		super(msg);
	}

	public ServiceLayerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceLayerException(Throwable cause) {
		super(cause.getMessage(),cause);
	}

}
