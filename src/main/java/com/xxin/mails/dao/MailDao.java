package com.xxin.mails.dao;

import com.xxin.mails.entity.Mail;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class MailDao {
    public int save(Mail mail){
       return DBUtils.save(mail);
    }
    public Mail queryByMid(String mid){
        Object o = DBUtils.query(null,"mid",mid,Mail.class);
        if (o!=null){
            return ((List<Mail>)o).get(0);
        }
        return null;
    }
    public int deleteBy(String key, String val){
        String[]keys = {key};
        String[]vals = {val};
        return DBUtils.deleteBy(keys,vals,Mail.class);
    }
}
