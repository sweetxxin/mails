package com.xxin.mails.dao;

import com.xxin.mails.entity.Recycle;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class RecycleDao {
    public int save(Recycle recycle){
        return DBUtils.save(recycle);
    }
    public List<Recycle> query(String key, String val){
        Object o = DBUtils.query(null,key,val,Recycle.class);
        if (o!=null){
            return (List<Recycle>) o;
        }
        return null;
    }
    public int deleteBy(String key, String val){
        String[]keys = {key};
        String[]vals = {val};
        return DBUtils.deleteBy(keys,vals,Recycle.class);
    }
}
