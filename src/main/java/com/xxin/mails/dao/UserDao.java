package com.xxin.mails.dao;

import com.xxin.mails.entity.User;
import com.xxin.mails.utils.DBUtils;

import java.util.List;

public class UserDao {
    public int save(User user){
        return DBUtils.save(user);
    }
    public User queryByUsername(String username){
        Object o = DBUtils.query(null,"username",username,User.class);
        if (o!=null){
            return ((List<User>) o).get(0);
        }else {
            return null;
        }
    }
    public User queryByUid(String uid){
        Object o = DBUtils.query(null,"uid",uid,User.class);
        if (o!=null){
            return ((List<User>) o).get(0);
        }else {
            return null;
        }
    }
    public int update(User user){
        String sql = "update user set username =  ? , name = ? , telephone = ? , mail = ? where uid = ?";
        System.out.println(user);
       return Integer.valueOf (DBUtils.doSql(sql, User.class, user.getUsername(),user.getName(),user.getTelephone(),user.getMail(),user.getUid()).toString());
    }
}
