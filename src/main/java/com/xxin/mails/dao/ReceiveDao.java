package com.xxin.mails.dao;

import com.xxin.mails.entity.Receive;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class ReceiveDao {
    public int save(Receive receive){
        return DBUtils.save(receive);
    }
    public Receive queryByRid(String rid){
        Object o = DBUtils.query(null,"rid",rid,Receive.class);
        if (o!=null){
            return ((List<Receive>)o).get(0);
        }
        return null;
    }
    public List<Receive> queryByMail(String mail){
        Object o = DBUtils.query(null,"`to`",mail,Receive.class);
        if (o!=null){
            return ((List<Receive>)o);
        }
        return null;
    }
    public List<Receive> queryByMid(String mid){
        Object o = DBUtils.query(null,"mid",mid,Receive.class);
        if (o!=null){
            return ((List<Receive>)o);
        }
        return null;
    }
    public List<Receive> query(String[]key, String[] val){
        Object o = DBUtils.query(null,key,val,Receive.class);
        if (o!=null){
            return ((List<Receive>)o);
        }
        return null;
    }
    public int count(String key, String val){
        return DBUtils.count(key, val, Receive.class);
    }
}
