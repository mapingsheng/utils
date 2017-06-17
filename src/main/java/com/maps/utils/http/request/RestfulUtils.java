package com.maps.utils.http.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 用于调用RESTful接口的工具类,
 *
 * @author 马平升
 * @version 1.0
 * @createDateTime 2016-9-21 23:14:55
 */
public class RestfulUtils {

	private static Logger logger = LoggerFactory.getLogger(RestfulUtils.class);
	
    /**
     * 空的私有构造函数
     */
    private RestfulUtils() {
    }

    /**
     * HTTP请求类型，GET
     */
    public static final HttpMethod GET = HttpMethod.GET;
    /**
     * HTTP请求类型，POST
     */
    public static final HttpMethod POST = HttpMethod.POST;
    /**
     * HTTP请求类型，DELETE
     */
    public static final HttpMethod DELETE = HttpMethod.DELETE;
    /**
     * HTTP请求类型，PUT
     */
    public static final HttpMethod PUT = HttpMethod.PUT;

    /**
     * HTTP请求类型，HEAd
     */
    public static final HttpMethod HEAD = HttpMethod.HEAD;
    
    /*读写超时时间，毫秒*/
    public static final int READTIMEOUT=600000;
    /*连接超时时间，毫秒*/
    public static final int CONNECTTIMEOUT=300000;
    
    /**
     * 调用RESTful Service接口，默认采用UTF-8编码接发数据。
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @return 接口的返回消息
     */
    public static String callService(String url, HttpMethod methodType) {
        return callService(url, methodType, null, null, "UTF-8", "UTF-8");
    }

    /**
     * 调用RESTful Service接口，默认采用UTF-8编码接发数据。
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @param data 需要向对方传输的消息，消息以Body的形式向对方传输
     * @return 接口的返回消息
     */
    public static String callService(String url, HttpMethod methodType, Object data) {
    	logger.info("开始请求并获取接口信息，接口地址："+url);
    	return callService(url, methodType, data, null, "UTF-8", "UTF-8");
    }

    /**
     * 调用RESTful Service接口，默认采用UTF-8编码接发数据。
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @param data 需要向对方传输的消息，消息以Body的形式向对方传输
     * @param headers 需要附加的头消息
     * @return 接口的返回消息
     */
    public static String callService(String url, HttpMethod methodType, Object data, Map<String, String> headers) {
    	logger.info("开始请求并获取接口信息，接口地址："+url);
        return callService(url, methodType, data, headers, "UTF-8", "UTF-8");
    }

    /**
     * 调用RESTful Service接口，默认采用UTF-8编码接收数据。
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @param data 需要向对方传输的消息，消息以Body的形式向对方传输
     * @param headers 需要附加的头消息
     * @param sendCharset 向外发送的数据的编码方式
     * @return 接口的返回消息
     */
    public static String callService(String url, HttpMethod methodType, Object data, Map<String, String> headers,
                                     String sendCharset) {
        return callService(url, methodType, data, headers, sendCharset, "UTF-8");
    }

    /**
     * 调用RESTful Service接口
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @param data 需要向对方传输的消息，消息以Body的形式向对方传输
     * @param headers 需要附加的头消息
     * @param sendCharset 向外发送的数据的编码方式
     * @param receiveCharset 接受的返回数据的编码方式
     * @return 接口的返回消息
     */
    public static String callService(String url, HttpMethod methodType, Object data, Map<String, String> headers,
                                     String sendCharset, String receiveCharset) {
        return new String(callResourceService(url, methodType, data, headers, sendCharset), Charset.forName(
                          receiveCharset));
    }

    /**
     * 调用RESTful Service接口
     *
     * @param url 在RESTful的URL地址
     * @param methodType 方法类型，如GET、POST、PUT等
     * @param data 需要向对方传输的消息，消息以Body的形式向对方传输
     * @param headers 需要附加的头消息
     * @param sendCharset 向外发送的数据的编码方式
     * @return 接口的返回消息
     */
    public static byte[] callResourceService(String url, HttpMethod methodType, Object data,
                                             Map<String, String> headers, String sendCharset) {
    	long start =  System.currentTimeMillis();
        Object parameters;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    	requestFactory.setReadTimeout(READTIMEOUT);
        requestFactory.setConnectTimeout(CONNECTTIMEOUT);
        
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter(Charset.forName(sendCharset)));
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restClient = new RestTemplate(messageConverters);
        restClient.setRequestFactory(requestFactory);
        
        HttpHeaders requestHeaders = new HttpHeaders();
        String type = MediaType.TEXT_HTML.getType();
        String subType = MediaType.TEXT_HTML.getSubtype();
        if (data instanceof Map) {
            type = MediaType.MULTIPART_FORM_DATA.getType();
            subType = MediaType.MULTIPART_FORM_DATA.getSubtype();
            Map map = new LinkedMultiValueMap();
            for (Object entry : ((Map) data).entrySet()) {
                Entry<String, Object> x = (Entry) entry;
                if (x.getValue() instanceof List) {
                    map.put(x.getKey(), x.getValue());
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(x.getValue());
                    map.put(x.getKey(), list);
                }
            }
            parameters = map;
        } else if (data instanceof ObjectNode || data instanceof ArrayNode) {
            type = MediaType.APPLICATION_JSON.getType();
            subType = MediaType.APPLICATION_JSON.getSubtype();
            parameters = ((JsonNode) data).toString();
        } else if (data instanceof String) {
            if (JsonUtils.testJson(data.toString())) {
                type = MediaType.APPLICATION_JSON.getType();
                subType = MediaType.APPLICATION_JSON.getSubtype();
            } else if (data.equals("")) {
                type = MediaType.APPLICATION_JSON.getType();
                subType = MediaType.APPLICATION_JSON.getSubtype();
            }
            parameters = data;
        } else if (data != null) {
            parameters = data.toString();
        } else {
            parameters = data;
        }
        Charset charset = Charset.forName(sendCharset);
        MediaType mediaType = new MediaType(type, subType, charset);
        requestHeaders.setContentType(mediaType);
        ArrayList<Charset> charsets = new ArrayList<>();
        charsets.add(charset);
        requestHeaders.setAcceptCharset(charsets);
        if (headers != null) {
            for (Map.Entry<String, String> keyValue : headers.entrySet()) {
                requestHeaders.add(keyValue.getKey(), keyValue.getValue());
            }
        }
        HttpEntity<Object> httpEntity = new HttpEntity<>(parameters, requestHeaders);
        ResponseEntity<byte[]> result = restClient.exchange(url, methodType, httpEntity, byte[].class);
        // return new String(result.getBody().getBytes(Charset.forName("ISO8859-1")), Charset.forName("UTF-8"));
        //    return new String(result.getBody().getBytes(Charset.forName(receiveCharset)), Charset.forName(receiveCharset));
        long end =  System.currentTimeMillis();
        logger.info("调用接口【"+url+"】完成；耗时【"+(end-start)+"】");
        return result.getBody();
    }

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        /*   String accountId = "8a48b5515493a1b70154cc413d4d3962";
        String accountAuth = "069cf474b8054b85a2d1e30253a021a3";
        String appId = "8a48b5515493a1b70154cc4cafed398d";
        String templateId = "1";
        String datatime = DateUtils.getTimestamp();
        String url = "https://sandboxapp.cloopen.com:8883/2013-12-26/Accounts/"
                             + accountId + "/SMS/TemplateSMS?sig=" + MD5Utils.encode(accountId + accountAuth + datatime);
        ObjectNode message = JsonUtils.getJsonMapper().createObjectNode();
        message.put("to", "13324592668");
        message.put("appId", appId);
        message.put("templateId", templateId);
        ArrayNode datas = message.putArray("datas");
        datas.add("今天吃饭不要钱");
        datas.add("你猜");
        HashMap<String, String> header = new HashMap<>();
        String authorization = Base64Utils.encode(accountId + ":" + datatime);
        System.out.println(authorization);
        header.put("Authorization", authorization);
        String x = callService(url, POST, message.toString(), header);
        System.out.println(new String(x.getBytes(), "GBK"));*/

        //  String url = "http://api.car.bitauto.com/CarInfo/getlefttreejson.ashx?tagtype=chexing";
        //  String y = callService(url, GET, "");
        // String x = y.substring(0, y.length() - 1).substring(14);
        //全局变量
        long term = new Date().getTime();
        String token = null;

        //基础信息定义
        ObjectMapper mapper = new ObjectMapper();
        String baseUrl = "https://a1.easemob.com/jinyiyanglaomessage2/myapp1/";
        String clientId = "YXA6xn_-AFmIEea-hmfSpB_mjQ";
        String clientSecret = "YXA6fgsUOtw0zP1JhUhUAFVzZpO2ofA";
        ObjectNode applyToken = mapper.createObjectNode();
        applyToken.put("grant_type", "client_credentials");
        applyToken.put("client_id", clientId);
        applyToken.put("client_secret", clientSecret);

        //测试循环10次
        for (int index = 0; index < 10; index++) {
            try {
                if (term <= new Date().getTime()) {
                    //申请Token
                    JsonNode result = mapper.readTree(callService(baseUrl + "token", POST, applyToken));
                    token = result.get("access_token").textValue();
                    term += result.get("expires_in").longValue() * 1000 - 3600;//有效期到期1个小时前
                    System.out.println("申请Token的返回数据为：" + result);
                    System.out.println("申请的Token为：" + token);
                }
                //正常发送消息
                String auth = "Bearer " + token;
                //设置头认证信息
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", auth);
                //设置消息体
                ObjectNode message = mapper.createObjectNode();
                message.put("target_type", "users");
                message.putArray("target").add("User102");
                message.put("from", "User101");

                ObjectNode msg = message.putObject("msg");
                msg.put("type", "txt");
                msg.put("msg", "aaaaa");

                System.out.println("向服务器发送的消息为："+message);
                System.out.println("服务器返回的消息为："+callService(baseUrl + "messages", POST, message, header));
                Thread.sleep(2000);
            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
