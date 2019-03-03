package com.xxin.mails.dao;

import com.xxin.mails.entity.Draft;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class DraftDao {
    public int save(Draft draft){
        return DBUtils.save(draft);
    }
    public List<Draft> query(String key, String val){
        Object o = DBUtils.query(null,key,val,Draft.class);
        if (o!=null){
            return (List<Draft>) o;
        }
        return null;
    }
    public int deleteBy(String key, String val){
        String[]keys = {key};
        String[]vals = {val};
        return DBUtils.deleteBy(keys,vals,Draft.class);
    }
}
