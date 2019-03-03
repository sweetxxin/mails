package com.xxin.mails.utils;


import com.xxin.mails.conf.Constants;
import org.apache.commons.mail.*;

import javax.mail.*;
import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

public class MailUtil {
    public static Folder connect(String addr, String auth, String protocol){
        Properties props = System.getProperties();
        if (protocol.equals("imap")){
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.imap.socketFactory.fallback", "false");
            props.setProperty("mail.transport.protocol", "imap"); // 使用的协议
            props.setProperty("mail.imap.port", "993");
            props.setProperty("mail.imap.socketFactory.port", "993");
        }else {
            props.put("mail.pop3.host", Util.chooseServer(addr,"smtp"));
            props.put("mail.pop3.auth", "true");
            //ssl加密
            props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.pop3.socketFactory.fallback", "true");
            props.setProperty("mail.pop3.socketFactory.port", "995");

        }
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        Folder folder = null;
        Store store = null;
        try {
            store = session.getStore(protocol);
            store.connect(Util.chooseServer(addr, protocol), addr,auth); // 登陆认证
            folder = store.getFolder("INBOX");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folder;

    }

    public static boolean sendComplexMail(String from, String to, String subject, Object content, String[] attach, String auth) {
        HtmlEmail mail = new HtmlEmail();
        mail.setHostName(Util.chooseServer(from,"smtp"));
        mail.setAuthentication(from, auth);
        mail.setSmtpPort(465);
        mail.setSSLOnConnect(true);
        mail.setSslSmtpPort("465");
        mail.setDebug(true);
        try {
            mail.setFrom(from);
            mail.addTo(to);
            mail.setCharset("UTF-8");
            mail.setSubject(subject);
            mail.setHtmlMsg("<html><body>"+content.toString()+"</body></html>");
            if (attach!=null){
                for (String filename:attach) {
                    mail.attach(addAttachment(filename));
                }
            }
            mail.setSentDate(new Date());
            mail.send();
        }catch (Exception e){
            System.out.printf("发送失败"+e.getMessage());
            return false;
        }
        return true;
    }
    private static EmailAttachment addAttachment(String attach){
        System.out.println(attach);
            EmailAttachment attachment = new EmailAttachment();
            File file = new File(new MailUtil().getClass().getResource("/").getPath());
            attachment.setPath(attach.replace(Constants.STATIC_RESOURCES_PATH, new File(file.getParent()).getParent()+"/static"));
        try {
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attach = attach.substring(attach.lastIndexOf("/")+1);
            attachment.setName(attach);
            return attachment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
