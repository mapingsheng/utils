package com.maps.utils.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
 
/**
 * XML工具类
 * 
 * @author mapingsheng
 * @since 2016-10-17
 */
public class XmlUtils {
    /**
     * 获取根节点
     * 
     * @param doc
     * @return
     */
    public static Element getRootElement(Document doc) {
        if (Objects.isNull(doc)) {
            return null;
        }
        return doc.getRootElement();
    }
 
    /**
     * 获取节点eleName下的文本值，若eleName不存在则返回默认值defaultValue
     * 
     * @param eleName
     * @param defaultValue
     * @return
     */
    public static String getElementValue(Element eleName, String defaultValue) {
        if (Objects.isNull(eleName)) {
            return defaultValue == null ? "" : defaultValue;
        } else {
            return eleName.getTextTrim();
        }
    }
 
    public static String getElementValue(String eleName, Element parentElement) {
        if (Objects.isNull(parentElement)) {
            return null;
        } else {
            Element element = parentElement.element(eleName);
            if (!Objects.isNull(element)) {
                return element.getTextTrim();
            } else {
                try {
                    throw new Exception("找不到节点" + eleName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
 
    /**
     * 获取节点eleName下的文本值
     * 
     * @param eleName
     * @return
     */
    public static String getElementValue(Element eleName) {
        return getElementValue(eleName, null);
    }
 
    public static Document read(File file) {
        return read(file, null);
    }
 
    public static Document findCDATA(Document body, String path) {
        return XmlUtils.stringToXml(XmlUtils.getElementValue(path,
                body.getRootElement()));
    }
 
    
    public static Document readForUtf8(File file) {
    	return read(file, "utf-8");
    }
    
    public static Document readForUtf8(InputStream in) {
    	return read(in, "utf-8");
    }
    
    
    /**
     * 
     * @param file
     * @param charset
     * @return
     * @throws DocumentException
     */
    public static Document read(InputStream in, String charset) {
        if (Objects.isNull(in)) {
            return null;
        }
        SAXReader reader = new SAXReader();
        if (Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }
        Document document = null;
        try {
            document = reader.read(in);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }
    
    /**
     * 
     * @param file
     * @param charset
     * @return
     * @throws DocumentException
     */
    public static Document read(File file, String charset) {
        if (Objects.isNull(file)) {
            return null;
        }
        SAXReader reader = new SAXReader();
        if (Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }
 
    public static Document read(URL url) {
        return read(url, null);
    }
 
    /**
     * 
     * @param url
     * @param charset
     * @return
     * @throws DocumentException
     */
    public static Document read(URL url, String charset) {
        if (Objects.isNull(url)) {
            return null;
        }
        SAXReader reader = new SAXReader();
        if (Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }
        Document document = null;
        try {
            document = reader.read(url);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }
 
    /**
     * 将文档树转换成字符串
     * 
     * @param doc
     * @return
     */
    public static String xmltoString(Document doc) {
        return xmltoString(doc, null);
    }
    
    /**
     * 
     * @param doc
     * @return
     * @throws IOException
     */
    public static String xmltoStringForUtf8(Document doc){
    	return xmltoString(doc, "UTF-8");
    }
 
    /**
     * 
     * @param doc
     * @param charset
     * @return
     * @throws IOException
     */
    public static String xmltoString(Document doc, String charset) {
        if (Objects.isNull(doc)) {
            return "";
        }
        if (Objects.isNull(charset)) {
            return doc.asXML();
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(charset);
        StringWriter strWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(strWriter, format);
        try {
            xmlWriter.write(doc);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strWriter.toString();
    }
 
    /**
     * 持久化Document
     * @param doc
     * @param charset
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static void xmltoFile(Document doc, File file, String charset)
            throws Exception {
        if (Objects.isNull(doc)) {
            throw new NullPointerException("doc cant not null");
        }
        if (Objects.isNull(charset)) {
            throw new NullPointerException("charset cant not null");
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(charset);
        FileOutputStream os = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(os, charset);
        XMLWriter xmlWriter = new XMLWriter(osw, format);
        try {
            xmlWriter.write(doc);
            xmlWriter.close();
            if (osw != null) {
                osw.close();
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 
     * @param doc
     * @param charset
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static void xmltoFile(Document doc, String filePath, String charset)
            throws Exception {
        xmltoFile(doc, new File(filePath), charset);
    }
 
     
    /**
     * 
     * @param doc
     * @param filePath
     * @param charset
     * @throws Exception
     */
    public static void writDocumentToFile(Document doc, String filePath, String charset)
            throws Exception {
        xmltoFile(doc, new File(filePath), charset);
    }
     
    public static Document stringToXml(String text) {
        try {
            return DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
     
    public static Document createDocument() {
        return DocumentHelper.createDocument();
    }
     
 
    public static void main(String[] args) {
    	String s_xmlpath="config/AchievementsYears.xml"; 
    	ClassLoader classLoader=XmlUtils.class.getClassLoader(); 
    	InputStream in=classLoader.getResourceAsStream(s_xmlpath);
    	
    	//File f = new File("/config/AchievementsYears.xml"); 
    	Document document = XmlUtils.readForUtf8(in);
    	Element root = document.getRootElement();
    	List<Element> childElements = root.elements();
    	 for (Element child : childElements) {
    		//未知属性名情况下
            /* List<Attribute> attributeList = child.attributes();
             for (Attribute attr : attributeList) {
                 System.out.println(attr.getName() + ": " + attr.getValue());
             }*/
    		 
    		 //已知属性名情况下
             System.out.println("name: " + child.attributeValue("name")+"  value: "+child.attributeValue("value"));
             
             //未知子元素名情况下
             /*List<Element> elementList = child.elements();
             for (Element ele : elementList) {
                 System.out.println(ele.getName() + ": " + ele.getText());
             }
             System.out.println();*/
              
             //已知子元素名的情况下
             /*System.out.println("title" + child.elementText("title"));
             System.out.println("author" + child.elementText("author"));*/
    	 }
    	
    }
}
