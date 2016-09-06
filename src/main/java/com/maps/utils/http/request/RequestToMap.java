package com.maps.utils.http.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by maps on 2016/9/6.
 * 
 * 将reques对象封装成map集合工具类，主要解决如下问题：
 * 1、当仅仅处理简单的form表单时，可以通过该工具类之间获取request对象中的属性集合，并且针对为“空”的属性可以达到集中处理并统一返回
 * 2、当仅仅处理带file的form表单时，提交到后台是已流的方式传输；解决通过request.getParameter不能获取属性的问题，并且可以集中统一处理
 */
public class RequestToMap {
	    /**
	     * 将request对象解析处理成map
	     * @param request
	     * @return
	     */
	    public static Map<String,String> transToMAP(HttpServletRequest request){
	        Map<String,String> returnMap = new HashMap<String,String>();
	        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	        
	        if (isMultipart){ //如果multipart/form-data类型的表单（带附件）
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload( factory );
                try {
                    List<FileItem> uploadItemList = upload.parseRequest(request);
                    for( FileItem uploadItem : uploadItemList ){
                        if( uploadItem.isFormField() ){  //普通form表单域
                        	
                            String fieldName = uploadItem.getFieldName();
                            String value = uploadItem.getString();
                            
                            returnMap.put(fieldName,value);
                         }else{  //form表单中的file域
                        	 
                            try{
                            	/**
                            	 * 1、从type="file"文件域中获取文件流、字节数组等等信息
                            	 */
                            	 String fieldname = uploadItem.getFieldName();
                                 String filename = FilenameUtils.getName(uploadItem.getName());
                                 InputStream filecontent = uploadItem.getInputStream();
                                 
                                 byte[] bytes = uploadItem.get();
                                 	
                                 /**
                             	 * 2、把对应的文件上传到文件服务器中，或者把文件写入磁盘上面
                             	 * 
                             	 * ........
                             	 */
                                 
                                 returnMap.put(fieldname,filename);//此处这是例子，具体根据你自己的业务封装
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    }
                } catch (FileUploadException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
	        }else{ //普通表单
	        	Map<String,String[]> map = request.getParameterMap();
                Iterator entries = map.entrySet().iterator();
                
                String name = "";
                String value = "";
                while (entries.hasNext()) {
                	Map.Entry entry = (Map.Entry) entries.next();
                    name = (String) entry.getKey();
                    Object valueObj = entry.getValue();
                    
                    if(null == valueObj){
                        value = "";
                    }else if(valueObj instanceof String[]){
                    	
                        String[] values = (String[])valueObj;
                        for(int i=0;i<values.length;i++){
                            value = values[i] + ",";
                        }
                        value = value.substring(0, value.length()-1);
                        
                    }else{
                        value = valueObj.toString();
                    }
                    returnMap.put(name, value);
                }
	        }

	        return  returnMap;
	    }
}
