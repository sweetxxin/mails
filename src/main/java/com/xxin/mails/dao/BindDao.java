package com.xxin.mails.dao;

import com.xxin.mails.entity.Bind;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class BindDao {
    public int save(Bind bind){
        return DBUtils.save(bind);
    }
    public List<Bind> queryBy(String key, String val){
        Object o = DBUtils.query(null,key,val,Bind.class);
        if (o!=null){
            List<Bind> binds = (List<Bind>) o;
            return binds;
        }
        return null;
    }
    public List<Bind> query(String[] key, String[] val){
        Object o = DBUtils.query(null, key, val, Bind.class);
        if (o!=null){
            List<Bind> binds = (List<Bind>) o;
            return binds;
        }
        return null;
    }
    public int remove(String uid, String mail){
        String[] keys = {"mail","uid"};
        String[] vals = {mail,uid};
        return DBUtils.deleteBy(keys,vals,Bind.class);
    }
}
