package com.xxin.mails.dao;

import com.xxin.mails.entity.Send;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class SendDao {
    public int save(Send send){
        return DBUtils.save(send);
    }
    public List<Send> queryBy(String[] key, String[] val){
        Object o = DBUtils.query(null,key,val,Send.class);
        if (o!=null){
            return (List<Send>) o;
        }else {
            return null;
        }
    }
    public int deleteBy(String key, String val){
        String[]keys = {key};
        String[]vals = {val};
        return DBUtils.deleteBy(keys,vals,Send.class);
    }
}
