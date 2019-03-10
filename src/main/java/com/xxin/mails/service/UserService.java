package com.xxin.mails.service;

import com.xxin.mails.dao.BindDao;
import com.xxin.mails.dao.UserDao;
import com.xxin.mails.entity.Bind;
import com.xxin.mails.entity.User;
import com.xxin.mails.utils.EncryptUtil;
import com.xxin.mails.utils.MailUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserService {
    private UserDao userDao = new UserDao();
    private BindDao bindDao = new BindDao();
    public int save(User user){
        return userDao.save(user);
    }
    public User queryByUsername(String username){
        return userDao.queryByUsername(username);
    }
    public int bind(String uid, String mail, String auth){
        String[] key = {"mail","uid"};
        String[] val = {mail,uid};
        List<Bind> b = bindDao.query(key,val);
        if (MailUtil.connect(mail,auth ,"imap" )==null){
            return -1;
        }
        if (b!=null){
            Bind bind = b.get(0);
            bind.setAuth(auth);
            return bindDao.save(bind);
        }else{
            Bind bind = new Bind();
            bind.setUid(uid);
            bind.setMail(mail);
            bind.setAuth(auth);
            return bindDao.save(bind);
        }
    }
    public int remove(String uid, String mail){
        return bindDao.remove(uid,mail);
    }
    public HashMap getUserInfo(String uid){
        HashMap map = new HashMap();
        User user = userDao.queryByUid(uid);
        List<Bind> binds = bindDao.queryBy("uid",uid);
        List list = new ArrayList();
        if (binds!=null){
            for (Bind bind:binds){
                list.add(bind.getMail());
            }
        }
        map.put("user",user);
        map.put("bind",list);
        return map;
    }
    public int update(User user){
        user.setPassword(EncryptUtil.SHA1(user.getPassword()));
        return userDao.update(user);
    }
}
