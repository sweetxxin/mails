package com.xxin.mails.utils;



import com.xxin.mails.conf.Constants;

import javax.sql.rowset.serial.SerialBlob;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static SerialBlob String2Blog(String s){
        try {
            return new SerialBlob(s.getBytes());
        } catch (Exception e) {
            System.out.println("blob报错"+e.getMessage());
        }
       return null;
    }
    public static String Blog2String(SerialBlob b){
        try {
            return new String(b.getBytes(1, (int) b.length()));
        } catch (Exception e) {
            System.out.println("blob报错"+e.getMessage());
        }
        return "";
    }
    public static String date2String(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    public static Date string2Date(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            System.out.println("转化时间"+s);
            return sdf.parse(s);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static String chooseServer(String addr, String protocol){
        if (protocol.equals("pop3")){
            if (addr.contains("@qq.com")){
                return Constants.QQ_MAIL_POP_HOST;
            }else if (addr.contains("@sina.com")){
                return Constants.SINA_MAIL_POP_HOST;
            }else if (addr.contains("@gmail.com")){
                return Constants.GOOGLE_MAIL_POP_HOST;
            }else if (addr.contains("@aliyun.com")){
                return Constants.ALI_MAIL_POP_HOST;
            }
        }else if (protocol.contains("smtp")){
            if (addr.contains("@qq.com")){
                return Constants.QQ_MAIL_HOST;
            }else if (addr.contains("@sina.com")){
                return Constants.SINA_MAIL_HOST;
            }else if (addr.contains("@gmail.com")){
                return Constants.GOOGLE_MAIL_HOST;
            }else if (addr.contains("@aliyun.com")){
                return Constants.ALI_MAIL_HOST;
            }
        } else {
            if (addr.contains("@qq.com")){
                return Constants.QQ_MAIL_IMAP_HOST;
            }else if (addr.contains("@sina.com")){
                return Constants.SINA_MAIL_IMAP_HOST;
            }else if (addr.contains("@gmail.com")){
                return Constants.GOOGLE_MAIL_IMAP_HOST;
            }else if (addr.contains("@aliyun.com")){
                return Constants.ALI_MAIL_IMAP_HOST;
            }
        }
        return Constants.QQ_MAIL_POP_HOST;
    }

    public static String getFileNameFromUrl(String url){
        String name = new Long(System.currentTimeMillis()).toString() + ".X";
        int index = url.lastIndexOf("/");
        if(index > 0){
            name = url.substring(index + 1);
            if(name.trim().length()>0){
                return name;
            }
        }
        return name;
    }

    public static String getFileExt(String f){
        int from = f.lastIndexOf(".");
        return f.substring(from );
    }
}
