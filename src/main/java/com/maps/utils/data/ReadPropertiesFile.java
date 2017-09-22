package com.xes.teacherrestserver.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 读取属性文件
 *
 * @author 弓永杰[gongyongjie@100tal.com]
 *
 * @version 
 *
 * @since 2016年11月24日
 */
public class ReadPropertiesFile {
	
	private static final Logger logger = LoggerFactory.getLogger(ReadPropertiesFile.class);

	private Properties properties = null;
	private String propertiesFilePath;

	public ReadPropertiesFile(String propertiesFilePath) {
		this.propertiesFilePath = propertiesFilePath;
		InputStream instream = null;
		try {
			properties = new Properties();
			properties.load(instream = getClass().getResourceAsStream(propertiesFilePath));
			logger.info("加载properties属性文件：" + propertiesFilePath + "...... ");
		} catch (Exception e) {
			logger.error("加载properties属性文件异常：" + propertiesFilePath + "...... ", e);
		} finally {
			try {
				if (instream != null)
					instream.close();
				logger.info("关闭properties属性文件流：" + propertiesFilePath + "...... ");
			} catch (IOException e) {
				logger.error("关闭properties属性文件异常：" + propertiesFilePath + "...... ", e);
			}
		}
	}

	


	/**
	 * 功能描述：根据key取得value
	 *
	 * @param key
	 * @return
	 *
	 * @author 弓永杰[gongyongjie@100tal.com]
	 *
	 * @since 2016年11月24日
	 *
	 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
	 */
	public String getValue(String key) {
		try {
			return properties.getProperty(key);
		} catch (Exception e) {
			logger.error("loading " + propertiesFilePath + "...... ");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 功能描述：把里边的内容转化为map
	 *
	 * @return
	 *
	 * @author 弓永杰[gongyongjie@100tal.com]
	 *
	 * @since 2016年11月24日
	 *
	 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
	 */
	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Set<Entry<Object, Object>> entrySet = properties.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				if (!entry.getKey().toString().startsWith("#")) {
					String key = entry.getKey().toString().trim();
					String value = entry.getValue().toString().trim();
					map.put(key, value);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error("loading " + propertiesFilePath + "...... ");
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * 功能描述：试当前功能类是否可用
	 *
	 * @param args
	 *
	 * @author 弓永杰[gongyongjie@100tal.com]
	 *
	 * @since 2016年11月24日
	 *
	 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
	 */
	public static void main(String[] args) {
		ReadPropertiesFile rpf = new ReadPropertiesFile("/property/member.properties");
		System.out.println(rpf.getValue("A"));

		Map<String, String> map = rpf.toMap();
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> entry : set) {
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
	}

}
