package com.maps.utils.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间工具类
 * @author mapingsheng
 *
 */
public class TimeUtil {
	
	/**
	 * 获取明天的当前日期串
	 * @return
	 */
	public static String getTomorrowCurrentDateStr(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, +1);
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());  
	}
	
	/**
	 * 获取明天的当前时间戳
	 * @return
	 */
	public static Long getTomorrowCurrentTime(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, +1);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 获取明天的整点日期串
	 * @return
	 */
	public static String getTomorrowDateStr(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, +1);
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());  
	}
	/**
	 * 获取明天的整点时间戳
	 * @return
	 */
	public static Long getTomorrowTime(){
		return getTomorrowTime(0,0,0);
	}
	/**
	 * 获取第二天的对应的时、分、秒
	 * @param hh 小时
	 * @param minute 分钟
	 * @param second 秒
	 * @return
	 */
	public static Long getTomorrowTime(int hh,int minute,int second){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, hh);
		cal.set(Calendar.MINUTE,minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, +1);
		return cal.getTimeInMillis();
	}
	
	
}
