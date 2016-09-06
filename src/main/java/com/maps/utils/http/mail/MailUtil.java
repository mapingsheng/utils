package com.maps.utils.http.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by maps on 2016/8/9.
 * 邮箱组件，提供发送邮件的功能方法
 */
public class MailUtil {
	    public static final String HOST = "smtp.XXXX.com"; //服务器
	    public static final String PROTOCOL = "smtp";
	    public static final int PORT = 25;
	    public static final String FROM = "XXXX@XXXX.com";//发件人的email
	    public static final String PWD = "XXXXXX";//发件人密码

	    /**
	     * 获取Session
	     * @return
	     */
	    private static Session getSession() {
	        Properties props = new Properties();
	        props.put("mail.smtp.host", HOST);//设置服务器地址
	        props.put("mail.store.protocol" , PROTOCOL);//设置协议
	        props.put("mail.smtp.port", PORT);//设置端口
	        props.put("mail.smtp.auth" , true);

	        Authenticator authenticator = new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(FROM, PWD);
	            }
	        };
	        Session session = Session.getDefaultInstance(props , authenticator);
	        return session;
	    }

	    public static void sendMail(String toEmail , String content){
	        sendMail(toEmail,null,content);
	    }

	    public static void sendMail(String toEmail , String title, String content) {
	        Session session = getSession();
	        title = null == title ? "活动++" :title;
	        try {
	            Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(FROM));
	            InternetAddress[] address = {new InternetAddress(toEmail)};
	            msg.setRecipients(Message.RecipientType.TO, address);
	            msg.setSubject(title);
	            msg.setContent(content , "text/html;charset=utf-8");
	            try {
	                msg.setSentDate(new Date());
	            } catch (MessagingException e) {
	                e.printStackTrace();
	            }
	            Transport.send(msg);
	        }
	        catch (MessagingException mex) {
	            mex.printStackTrace();
	        }
	    }

	   public static void main(String[] args){
	        MailUtil.sendMail("503981739@qq.com","注册邮箱验证","你好啊！");
	   }
}
