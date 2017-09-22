package com.xes.teacherrestserver.base.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;



/**
 * URL方式访问REST接口
 * 
 * @author 王照瑞-lenovo
 * 
 */
public class RestMethodUtil {
	private static final Logger logger = LoggerFactory.getLogger(RestMethodUtil.class);
	private static final int CONNECT_TIMEOUT = 3000;
	private static final int READ_TIMEOUT = 60000;
	/**
	 * 默认系统字符集编码：UTF-8
	 */
	private static final String DEFAULT_CHARACTERENCODING = "UTF-8";
	// /* valid HTTP methods */
	public static enum HttpMethods {
		GET("GET"), POST("POST"), HEAD("HEAD"), OPTIONS("OPTIONS"), PUT("PUT"), DELETE("DELETE"), TRACE("TRACE");
		public final String title;

		HttpMethods(String title) {
			this.title = title;
		}

		public String getMethodValue() {
			return this.title;
		}
	};

	public static String invok(URL url, HttpMethods method, Map<String, String> parameter, Map<String, String> header) throws Exception {
		String value = null;
		if (HttpMethods.GET.equals(method)) {
			value = getMethod(url, parameter, header);
		} else if (HttpMethods.POST.equals(method)) {
			value = postMethod(url, parameter, header);
		}
		return value;
	}
	
	public static String invok(URL url, HttpMethods method, Map<String, String> parameter, Map<String, String> header, String timeOut) throws Exception {
        String value = null;
        if (HttpMethods.GET.equals(method)) {
            value = getMethod(url, parameter, header);
        } else if (HttpMethods.POST.equals(method)) {
            value = postMethod(url, parameter, header);
        }
        return value;
    }

	private static String getMethod(URL url, Map<String, String> parameter, Map<String, String> header) throws Exception {
		StringBuilder sbParameter = new StringBuilder();
		if (parameter != null && parameter.size() > 0) {
			sbParameter.append("?");
			Set<Entry<String, String>> parameterSet = parameter.entrySet();
			for (Entry<String, String> entry : parameterSet) {
				String parameterKey = entry.getKey();
				String parameterValue = entry.getValue();
				if(StringUtils.isEmpty(parameterValue)) continue;
				sbParameter.append(parameterKey + "=" + URLEncoder.encode(parameterValue, DEFAULT_CHARACTERENCODING) + "&");// 加入提交参数
			}
		}
		String urlStr = url.toString() + sbParameter.toString();

		HttpURLConnection hc = (HttpURLConnection) new URL(urlStr).openConnection();
		hc.setConnectTimeout(CONNECT_TIMEOUT);
		hc.setReadTimeout(READ_TIMEOUT);
 
		Set<Entry<String, String>> headerSet = header.entrySet();
		for (Entry<String, String> entry : headerSet) {
			String headerKey = entry.getKey();
			String headerValue = entry.getValue();
			hc.setRequestProperty(headerKey, headerValue);
		}

		hc.setUseCaches(false);
		hc.setDoOutput(true);
		hc.setDoInput(true);
		hc.setInstanceFollowRedirects(true);// 自动重定向

		hc.setRequestMethod(HttpMethods.GET.getMethodValue());

		int code = hc.getResponseCode();
		logger.info("ResponseCode:" + code);
		StringBuilder sb = new StringBuilder();
		if (code == 200) {// OK
			InputStream is = null;
			InputStreamReader isr = null;
			ByteArrayOutputStream baos = null;
			//BufferedReader br = null;
			try {
				is = hc.getInputStream();
				byte[] buf= new byte[1024*1024];
				int pos = -1;
				baos = new ByteArrayOutputStream();
				while((pos = is.read(buf))!=-1){
					baos.write(buf, 0, pos);
				}
				sb.append(new String(baos.toByteArray(), DEFAULT_CHARACTERENCODING));
//				isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
//				br = new BufferedReader(isr);
//				String line = null;
//				while ((line = br.readLine()) != null) {
//					sb.append(line);
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (baos != null)
						baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (isr != null)
						isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {// 得到错误信息流
			printErrorStream(hc);
		}
		return sb.toString();
	}

	private static void printErrorStream(HttpURLConnection hc) {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			is = hc.getErrorStream();
			if(null != is){
				isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
				br = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (isr != null)
					isr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String postMethod(URL url, Map<String, String> parameter, Map<String, String> header) throws Exception {
		HttpURLConnection hc = (HttpURLConnection) url.openConnection();
		hc.setConnectTimeout(CONNECT_TIMEOUT);
		hc.setReadTimeout(READ_TIMEOUT);

		Set<Entry<String, String>> headerSet = header.entrySet();
		for (Entry<String, String> entry : headerSet) {
			String headerKey = entry.getKey();
			String headerValue = entry.getValue();
			hc.setRequestProperty(headerKey, headerValue);
		}

		hc.setUseCaches(false);
		hc.setDoOutput(true);
		hc.setDoInput(true);
		hc.setInstanceFollowRedirects(true);// 自动重定向

		hc.setRequestMethod(HttpMethods.POST.getMethodValue());

		if (parameter != null) {
			DataOutputStream submitDos = new DataOutputStream(hc.getOutputStream());
			Set<Entry<String, String>> parameterSet = parameter.entrySet();
			for (Entry<String, String> entry : parameterSet) {
				String parameterKey = entry.getKey();
				String parameterValue = entry.getValue();
				submitDos.writeBytes(parameterKey + "=" + URLEncoder.encode((StringUtils.isBlank(parameterValue)?"":parameterValue), DEFAULT_CHARACTERENCODING) + "&");// 加入提交参数
			}
			submitDos.flush();
			submitDos.close();
		}

		int code = hc.getResponseCode();
		logger.info("ResponseCode:" + code);
		StringBuilder sb = new StringBuilder();
		if (code == 200) {// OK
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				is = hc.getInputStream();
				isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
				br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (isr != null)
						isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {// 得到错误信息流
			printErrorStream(hc);
		}
		return sb.toString();
	}

	/**
	 * multipart/form-data 方式上传文件[多文件方式]
	 * @param url
	 * @param parameter
	 * @param header
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public static String invok(URL url, Map<String, String> parameter, Map<String, String> header, MultipartFile[] files) throws Exception {
		String boundary = Long.toString(System.currentTimeMillis(), 16);
		URLConnection uc = url.openConnection();
		HttpURLConnection con = (HttpURLConnection) uc;
		con.setConnectTimeout(CONNECT_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");

		if(header != null){
			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				String headerKey = entry.getKey();
				String headerValue = entry.getValue();
				con.setRequestProperty(headerKey, headerValue);
			}
		}

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = con.getOutputStream();
			dout = new DataOutputStream(out);
			
			dout.writeBytes("--" + boundary + "\r\n");
			dout.writeBytes("multipart/form-data; boundary=--" + boundary + "\r\n");
			if(parameter != null) {
				Set<Entry<String, String>> parameterSet = parameter.entrySet();
				for (Entry<String, String> entry : parameterSet) {
					String parameterKey = entry.getKey();
					String parameterValue = entry.getValue();
					dout.writeBytes("----" + boundary + "\r\n");// 用户名信息
					dout.writeBytes("Content-Disposition: form-data; name=\""+parameterKey+"\"\r\n");
					dout.writeBytes("Content-Type: text/plain; charset="+DEFAULT_CHARACTERENCODING+"\r\n");
					dout.writeBytes("Content-Transfer-Encoding: 8bit\r\n");
					dout.writeBytes("\r\n");
					dout.write(parameterValue!=null?parameterValue.getBytes(DEFAULT_CHARACTERENCODING):"".getBytes(DEFAULT_CHARACTERENCODING));
					dout.writeBytes("\r\n");
				}
			}
			if(files!=null){
				for (MultipartFile attachmentFile : files) {
					if (!attachmentFile.isEmpty()) {
						String filename = attachmentFile.getOriginalFilename();
						dout.writeBytes("----" + boundary + "\r\n");
						dout.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" +URLEncoder.encode(filename, DEFAULT_CHARACTERENCODING) + "\"\r\n");
						dout.writeBytes("Content-Type: application/octet-stream\r\n");
						dout.writeBytes("Content-Transfer-Encoding: binary\r\n");
						dout.writeBytes("\r\n");
						InputStream source = null;
						try {
							source = attachmentFile.getInputStream();
							IOUtils.copy(source, dout);
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							IOUtils.closeQuietly(source);
						}
						dout.writeBytes("\r\n");
					}
				}
			}
			dout.writeBytes("----" + boundary + "--\r\n");
			dout.flush();
			dout.close();
			out.close();
			int code = con.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				StringBuilder sb = new StringBuilder();
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					is = con.getInputStream();
					isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (isr != null)
							isr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				printErrorStream(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dout != null)
					dout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * multipart/form-data 方式上传文件[单文件方式]
	 * @param url
	 * @param parameter
	 * @param header
	 * @param attachmentFile
	 * @param formFieldName 附件文件表单字段名
	 * @return
	 * @throws Exception
	 */
	public static String invok(URL url, Map<String, String> parameter, Map<String, String> header, MultipartFile attachmentFile, String formFieldName) throws Exception {
		String boundary = Long.toString(System.currentTimeMillis(), 16);
		URLConnection uc = url.openConnection();
		HttpURLConnection con = (HttpURLConnection) uc;
		con.setConnectTimeout(CONNECT_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");

		if(header != null){
			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				String headerKey = entry.getKey();
				String headerValue = entry.getValue();
				con.setRequestProperty(headerKey, headerValue);
			}
		}

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = con.getOutputStream();
			dout = new DataOutputStream(out);
			
			dout.writeBytes("--" + boundary + "\r\n");
			dout.writeBytes("multipart/form-data; boundary=--" + boundary + "\r\n");
			if(parameter != null) {
				Set<Entry<String, String>> parameterSet = parameter.entrySet();
				for (Entry<String, String> entry : parameterSet) {
					String parameterKey = entry.getKey();
					String parameterValue = entry.getValue();
					dout.writeBytes("----" + boundary + "\r\n");// 用户名信息
					dout.writeBytes("Content-Disposition: form-data; name=\""+parameterKey+"\"\r\n");
					dout.writeBytes("Content-Type: text/plain; charset="+DEFAULT_CHARACTERENCODING+"\r\n");
					dout.writeBytes("Content-Transfer-Encoding: 8bit\r\n");
					dout.writeBytes("\r\n");
					dout.write(parameterValue!=null?parameterValue.getBytes(DEFAULT_CHARACTERENCODING):"".getBytes(DEFAULT_CHARACTERENCODING));
					dout.writeBytes("\r\n");
				}
			}
			if(attachmentFile!=null && !attachmentFile.isEmpty()){
				String filename = attachmentFile.getOriginalFilename();
				dout.writeBytes("----" + boundary + "\r\n");
				dout.writeBytes("Content-Disposition: form-data; name=\""+formFieldName+"\"; filename=\"" +URLEncoder.encode(filename, DEFAULT_CHARACTERENCODING) + "\"\r\n");
				dout.writeBytes("Content-Type: application/octet-stream\r\n");
				dout.writeBytes("Content-Transfer-Encoding: binary\r\n");
				dout.writeBytes("\r\n");
				InputStream source = null;
				try {
					source = attachmentFile.getInputStream();
					IOUtils.copy(source, dout);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(source);
				}
				dout.writeBytes("\r\n");
			}
			dout.writeBytes("----" + boundary + "--\r\n");
			dout.flush();
			dout.close();
			out.close();
			int code = con.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				StringBuilder sb = new StringBuilder();
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					is = con.getInputStream();
					isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (isr != null)
							isr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				printErrorStream(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dout != null)
					dout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * multipart/form-data 方式上传文件[File方式]
	 * @param url
	 * @param parameter
	 * @param header
	 * @param file
	 * @param formFieldName 附件文件表单字段名
	 * @return
	 * @throws Exception
	 */
	public static String invok(URL url, Map<String, String> parameter, Map<String, String> header, File file, String formFieldName) throws Exception {
		String boundary = Long.toString(System.currentTimeMillis(), 16);
		URLConnection uc = url.openConnection();
		HttpURLConnection con = (HttpURLConnection) uc;
		con.setConnectTimeout(CONNECT_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");

		if(header != null){
			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				String headerKey = entry.getKey();
				String headerValue = entry.getValue();
				con.setRequestProperty(headerKey, headerValue);
			}
		}

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = con.getOutputStream();
			dout = new DataOutputStream(out);
			
			dout.writeBytes("--" + boundary + "\r\n");
			dout.writeBytes("multipart/form-data; boundary=--" + boundary + "\r\n");
			if(parameter != null) {
				Set<Entry<String, String>> parameterSet = parameter.entrySet();
				for (Entry<String, String> entry : parameterSet) {
					String parameterKey = entry.getKey();
					String parameterValue = entry.getValue();
					dout.writeBytes("----" + boundary + "\r\n");// 用户名信息
					dout.writeBytes("Content-Disposition: form-data; name=\""+parameterKey+"\"\r\n");
					dout.writeBytes("Content-Type: text/plain; charset="+DEFAULT_CHARACTERENCODING+"\r\n");
					dout.writeBytes("Content-Transfer-Encoding: 8bit\r\n");
					dout.writeBytes("\r\n");
					dout.write(parameterValue!=null?parameterValue.getBytes(DEFAULT_CHARACTERENCODING):"".getBytes(DEFAULT_CHARACTERENCODING));
					dout.writeBytes("\r\n");
				}
			}
			if(file!=null && file.isFile()){
				String filename = file.getName();
				dout.writeBytes("----" + boundary + "\r\n");
				dout.writeBytes("Content-Disposition: form-data; name=\""+formFieldName+"\"; filename=\"" +URLEncoder.encode(filename, DEFAULT_CHARACTERENCODING) + "\"\r\n");
				dout.writeBytes("Content-Type: application/octet-stream\r\n");
				dout.writeBytes("Content-Transfer-Encoding: binary\r\n");
				dout.writeBytes("\r\n");
				InputStream source = null;
				try {
					source = new FileInputStream(file);
					IOUtils.copy(source, dout);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(source);
				}
				dout.writeBytes("\r\n");
			}
			dout.writeBytes("----" + boundary + "--\r\n");
			dout.flush();
			dout.close();
			out.close();
			int code = con.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				StringBuilder sb = new StringBuilder();
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					is = con.getInputStream();
					isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (isr != null)
							isr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				printErrorStream(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dout != null)
					dout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * multipart/form-data 方式上传文件[BufferedImage方式]
	 * @param url
	 * @param parameter
	 * @param header
	 * @param bufferedImage
	 * @param formFieldName 附件文件表单字段名
	 * @return
	 * @throws Exception
	 */
	public static String invok(URL url, Map<String, String> parameter, Map<String, String> header, BufferedImage bufferedImage,String fileType, String formFieldName) throws Exception {
		String boundary = Long.toString(System.currentTimeMillis(), 16);
		URLConnection uc = url.openConnection();
		HttpURLConnection con = (HttpURLConnection) uc;
		con.setConnectTimeout(CONNECT_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");

		if(header != null){
			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				String headerKey = entry.getKey();
				String headerValue = entry.getValue();
				con.setRequestProperty(headerKey, headerValue);
			}
		}

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = con.getOutputStream();
			dout = new DataOutputStream(out);
			
			dout.writeBytes("--" + boundary + "\r\n");
			dout.writeBytes("multipart/form-data; boundary=--" + boundary + "\r\n");
			if(parameter != null) {
				Set<Entry<String, String>> parameterSet = parameter.entrySet();
				for (Entry<String, String> entry : parameterSet) {
					String parameterKey = entry.getKey();
					String parameterValue = entry.getValue();
					dout.writeBytes("----" + boundary + "\r\n");// 用户名信息
					dout.writeBytes("Content-Disposition: form-data; name=\""+parameterKey+"\"\r\n");
					dout.writeBytes("Content-Type: text/plain; charset="+DEFAULT_CHARACTERENCODING+"\r\n");
					dout.writeBytes("Content-Transfer-Encoding: 8bit\r\n");
					dout.writeBytes("\r\n");
					dout.write(parameterValue!=null?parameterValue.getBytes(DEFAULT_CHARACTERENCODING):"".getBytes(DEFAULT_CHARACTERENCODING));
					dout.writeBytes("\r\n");
				}
			}
			if(bufferedImage!=null){
				 
				String filename = UUID.randomUUID().toString() + "."+fileType;
				dout.writeBytes("----" + boundary + "\r\n");
				dout.writeBytes("Content-Disposition: form-data; name=\""+formFieldName+"\"; filename=\"" +URLEncoder.encode(filename, DEFAULT_CHARACTERENCODING) + "\"\r\n");
				dout.writeBytes("Content-Type: application/octet-stream\r\n");
				dout.writeBytes("Content-Transfer-Encoding: binary\r\n");
				dout.writeBytes("\r\n");
				 
				try {
					ImageIO.write(bufferedImage, fileType, dout);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dout.writeBytes("\r\n");
			}
			dout.writeBytes("----" + boundary + "--\r\n");
			dout.flush();
			dout.close();
			out.close();
			int code = con.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				StringBuilder sb = new StringBuilder();
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					is = con.getInputStream();
					isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (isr != null)
							isr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				printErrorStream(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dout != null)
					dout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * multipart/form-data 方式上传文件[单文件方式]
	 * @param url
	 * @param parameter
	 * @param header
	 * @param inputStream
	 * @param fileName
	 * @param formFieldName 附件文件表单字段名
	 * @return
	 * @throws Exception
	 */
	public static String invok(URL url, Map<String, String> parameter, Map<String, String> header, InputStream inputStream, String fileName, String formFieldName) throws Exception {
		String boundary = Long.toString(System.currentTimeMillis(), 16);
		URLConnection uc = url.openConnection();
		HttpURLConnection con = (HttpURLConnection) uc;
		con.setConnectTimeout(CONNECT_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");

		if(header != null){
			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				String headerKey = entry.getKey();
				String headerValue = entry.getValue();
				con.setRequestProperty(headerKey, headerValue);
			}
		}

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = con.getOutputStream();
			dout = new DataOutputStream(out);
			
			dout.writeBytes("--" + boundary + "\r\n");
			dout.writeBytes("multipart/form-data; boundary=--" + boundary + "\r\n");
			if(parameter != null) {
				Set<Entry<String, String>> parameterSet = parameter.entrySet();
				for (Entry<String, String> entry : parameterSet) {
					String parameterKey = entry.getKey();
					String parameterValue = entry.getValue();
					dout.writeBytes("----" + boundary + "\r\n");// 用户名信息
					dout.writeBytes("Content-Disposition: form-data; name=\""+parameterKey+"\"\r\n");
					dout.writeBytes("Content-Type: text/plain; charset="+DEFAULT_CHARACTERENCODING+"\r\n");
					dout.writeBytes("Content-Transfer-Encoding: 8bit\r\n");
					dout.writeBytes("\r\n");
					dout.write(parameterValue!=null?parameterValue.getBytes(DEFAULT_CHARACTERENCODING):"".getBytes(DEFAULT_CHARACTERENCODING));
					dout.writeBytes("\r\n");
				}
			}
			if(inputStream!=null && !StringUtils.isEmpty(fileName)){
				String filename = fileName;
				dout.writeBytes("----" + boundary + "\r\n");
				dout.writeBytes("Content-Disposition: form-data; name=\""+formFieldName+"\"; filename=\"" +URLEncoder.encode(filename, DEFAULT_CHARACTERENCODING) + "\"\r\n");
				dout.writeBytes("Content-Type: application/octet-stream\r\n");
				dout.writeBytes("Content-Transfer-Encoding: binary\r\n");
				dout.writeBytes("\r\n");
				try {
					IOUtils.copy(inputStream, dout);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dout.writeBytes("\r\n");
			}
			dout.writeBytes("----" + boundary + "--\r\n");
			dout.flush();
			dout.close();
			out.close();
			int code = con.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				StringBuilder sb = new StringBuilder();
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					is = con.getInputStream();
					isr = new InputStreamReader(is, DEFAULT_CHARACTERENCODING);
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (isr != null)
							isr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				printErrorStream(con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dout != null)
					dout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 从指写URL中下载文件
	 * @param url
	 * @return
	 */
	public static byte[] downloadAttachmentByUrl(String url) {
		if(StringUtils.isEmpty(url)) return null;
		try{
			HttpURLConnection hc = (HttpURLConnection) new URL(url).openConnection();
			hc.setConnectTimeout(CONNECT_TIMEOUT);
			hc.setReadTimeout(READ_TIMEOUT);
			
			hc.setUseCaches(false);
			hc.setDoOutput(true);
			hc.setDoInput(true);
			hc.setInstanceFollowRedirects(true);// 自动重定向
			hc.setRequestMethod(HttpMethods.GET.getMethodValue());
			
			int code = hc.getResponseCode();
			logger.info("ResponseCode:" + code);
			if (code == 200) {// OK
				InputStream is = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					is = hc.getInputStream();
					IOUtils.copy(is, baos);
					return baos.toByteArray();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(baos);
				}
			} else {// 得到错误信息流
				printErrorStream(hc);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 构建业务系统请求Header参数
	 * 
	 * @param validateKey
	 *            需要进行MD5验证的方法KEY
	 * @return
	 */
	public static Map<String, String> buildBusinessHeader(String validateKey) {
		Map<String, String> header = new HashMap<String, String>();

		String validate = "" + validateKey;
		header.put("plat", "webim");
		header.put("sys", "java");
		header.put("md5", StringUtil.buildMD5Value(validate + header.get("plat") + header.get("sys") + "com.xes.employee"));

		return header;
	}
	
	public static Map<String, String> buildFourthHeader(String validateKey) {
		Map<String, String> header = new HashMap<String, String>();

		String validate = "" + validateKey;
		header.put("area", validate);
		header.put("secret", "ff8080814cb2f51c014cbf8768520125");
		header.put("cv", "4.2.0");
		header.put("csys", "java");
		header.put("cflat", "3");
		header.put("timestamp", ""+new Date().getTime());
		return header;
	}

	/**
	 * 构建家长会系统请求Header参数
	 * 
	 * @param validateKey
	 * @return
	 */
	public static Map<String, String> buildPatriarchHeader(String validateKey) {
		Map<String, String> header = new HashMap<String, String>();

		String validate = "" + validateKey;
		header.put("plat", "webim");
		header.put("sys", "java");
		header.put("md5", StringUtil.buildMD5Value(validate + header.get("plat") + header.get("sys") + "com.xes.jzh"));

		return header;
	}
	
	public static Map<String, String> buildPatriarchHeaderNetease(){
	    
	    Map<String, String> header = new HashMap<String, String>();
	    String validate = "";
	    header.put("plat", "java");
	    header.put("sys", "jzh");
	    header.put("key", "com.xes.jzh");
	    header.put("md5", StringUtil.buildMD5Value(validate + header.get("plat") + header.get("sys") + header.get("key")));
	    return header;
	}
	
	public static Map<String, String> buildMessageServerHeader(String validateKey) {
		Map<String, String> header = new HashMap<String, String>();

		header.put("secret", validateKey);

		return header;
	}

	/**
	 * List属性定义的泛型类型
	 * 
	 * @param field
	 * @return 返回null表示传入的属性无定义的泛型
	 */
	public static Class<?> genericClassForList(Field field) {
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			return (Class<?>) pt.getActualTypeArguments()[0];
		}
		return null;
	}

	/**
	 * 将JSONObject对象转换成指定的JAVA对象， 只有JSONObject对象的键值与JAVA对象的属性名称与类型完全相同时才可赋值成功
	 * 否则得到的JAVA对象的值都不存在或者为默认值。 JAVA对象必须提供无参构造方法，不支持Map属性
	 * 
	 * @param jsonObject
	 * @param classz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseJSONObjectToBean(JSONObject jsonObject, Class<T> classz) {
		Set<String> jsonKeyNamesSet = new HashSet<String>();
		String[] jsonKeyNames = JSONObject.getNames(jsonObject);
		if(jsonKeyNames != null) jsonKeyNamesSet.addAll(Arrays.asList(jsonKeyNames));

		if (classz.isAssignableFrom(Map.class)) {// 支持MAP类型转化
			@SuppressWarnings("rawtypes")
			Map bean = null;
			try {
				bean = HashMap.class.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (bean != null)
				for (String jsonKeyName : jsonKeyNames) {
					Object jov = null;
					try {
						jov = jsonObject.get(jsonKeyName);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					if (JSONObject.NULL.equals(jov))
						jov = null;
					bean.put(jsonKeyName, jov);
				}
			return (T) bean;
		}
		// 默认使用，用户自定义类型
		T bean = null;
		String className = classz.getName();
		if(className.indexOf("$")!=-1){
			Constructor<?>[] $Constructor = classz.getConstructors();
			for(Constructor<?> constructor:$Constructor){
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				try {
					Object[] initargs = new Object[parameterTypes.length];
					for(int index=0; index<parameterTypes.length; index++){
						Class<?> cz = parameterTypes[index];
						initargs[index] = cz.newInstance();
					}
					bean = (T) constructor.newInstance(initargs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				bean = classz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Field[] beanFields = classz.getDeclaredFields();
		if (bean != null && beanFields != null)
			for (Field field : beanFields) {
				String attributeName = field.getName();
				Class<?> attributeClass = field.getType();// 得到属性定义的类型
				if (jsonKeyNamesSet.contains(attributeName)) {
					Object jsonValue = null;
					try {
						jsonValue = jsonObject.get(attributeName);
						if (JSONObject.NULL.equals(jsonValue)) {
							jsonValue = null;
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					if (jsonValue instanceof JSONObject) {
						JSONObject jo = (JSONObject) jsonValue;
						jsonValue = parseJSONObjectToBean(jo, attributeClass);// 处理JSONObject
					} else if (jsonValue instanceof JSONArray) {
						JSONArray ja = (JSONArray) jsonValue;
						if (attributeClass.isArray()) {// 如果对象属性是数组
							Class<?> arrayAttributeClass = attributeClass.getComponentType();// 数组属性定义的数据类型
							jsonValue = parseJSONArrayToBean(ja, arrayAttributeClass);
						} else if (attributeClass.isAssignableFrom(List.class)) {// 对象属性是List类型
							Class<?> arrayAttributeClass = genericClassForList(field);// 得到List的泛型类型
							jsonValue = Arrays.asList(parseJSONArrayToBean(ja, arrayAttributeClass));
						} else {
							jsonValue = null;
						}
					}
					field.setAccessible(true);
					try {
						if (String.class.equals(attributeClass)) {
							field.set(bean, jsonValue != null ? jsonValue.toString() : null);
						} else if(Long.class.equals(attributeClass)){
							field.set(bean, Long.valueOf(jsonValue!=null?jsonValue.toString():"0"));
						} else if(List.class.equals(attributeClass)){
							field.set(bean, jsonValue==null||StringUtils.isEmpty(jsonValue.toString())?null:jsonValue);
						} else {
							field.set(bean, jsonValue);					
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		return bean;
	}

	/**
	 * 将JSONArray转换成指定的JAVA对象数组
	 * 
	 * @param jsonArray
	 * @param classz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] parseJSONArrayToBean(JSONArray jsonArray, Class<T> classz) {
		int arrayLength = jsonArray.length();
		Object arrayObjectValue = Array.newInstance(classz, arrayLength);// 实例化数组
		for (int index = 0; index < arrayLength; index++) {
			Object jaValue = null;
			try {
				jaValue = jsonArray.get(index);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Object avo = null;
			if (jaValue instanceof JSONObject) {
				JSONObject jo = (JSONObject) jaValue;
				avo = parseJSONObjectToBean(jo, classz);// 处理JSONObject
			} else if (jaValue instanceof JSONArray) {// 表示是一个多维数组，或不规则数组
				// 不规则数据，暂不支持转换
			} else if(jaValue != JSONObject.NULL){
				avo = jaValue;
			}
			Array.set(arrayObjectValue, index, avo);// 基本类型赋值
		}
		return (T[]) arrayObjectValue;
	}
	
	public static String postJSONObject(URL url, JSONObject parameter, Map<String, String> header) throws Exception {
		HttpURLConnection hc = (HttpURLConnection) url.openConnection();
		hc.setConnectTimeout(CONNECT_TIMEOUT);
		hc.setReadTimeout(READ_TIMEOUT);
		Set<Entry<String, String>> headerSet = header.entrySet();
		for (Entry<String, String> entry : headerSet) {
			String headerKey = entry.getKey();
			String headerValue = entry.getValue();
			hc.setRequestProperty(headerKey, headerValue);
		}
		hc.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		hc.setUseCaches(false);
		hc.setDoOutput(true);
		hc.setDoInput(true);
		hc.setInstanceFollowRedirects(true);

		hc.setRequestMethod(HttpMethods.POST.getMethodValue());

		if (parameter != null) {
			DataOutputStream submitDos = new DataOutputStream(hc.getOutputStream());
			submitDos.write(parameter.toString().getBytes("utf-8"));
			submitDos.flush();
			submitDos.close();
		}

		int code = hc.getResponseCode();
		System.out.println(code);
		StringBuilder sb = new StringBuilder();
		if (code == 200) {// OK
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				is = hc.getInputStream();
				isr = new InputStreamReader(is, "UTF-8");
				br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			 
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (isr != null)
						isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			printErrorStream(hc);
			System.out.println(hc);
		}
		return sb.toString();
	}
	
	public static String buildQuestionsLibraryAppKey(String validateKey){
		return StringUtil.buildMD5Value(validateKey + "634b3ded7eba8706");//634b3ded7eba8706 涓庢櫤搴风害瀹氱殑KEY
	}
}
