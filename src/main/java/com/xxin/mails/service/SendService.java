package com.xxin.mails.service;

import com.xxin.mails.dao.*;
import com.xxin.mails.entity.*;
import com.xxin.mails.utils.DBUtils;
import com.xxin.mails.utils.MailUtil;
import com.xxin.mails.utils.Util;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class SendService {
    private MailDao mailDao = new MailDao();
    private SendDao sendDao = new SendDao();
    private BindDao bindDao = new BindDao();
    private DraftDao draftDao = new DraftDao();
    private RecycleDao recycleDao = new RecycleDao();
    private AttachmentDao attachmentDao = new AttachmentDao();

    public int saveMail(Mail mail){
        return mailDao.save(mail);
    }
    public int saveSend(Send send){
        return sendDao.save(send);
    }
    public int send(Mail mail, Send send, String[] attachments, String at) {
        try {
            DBUtils.getConnection().setAutoCommit(false);
            String mid = UUID.randomUUID().toString();
            String sid = UUID.randomUUID().toString();
            mail.setMid(mid);
            send.setSid(sid);
            send.setMid(mid);
            send.setStatus("sending");
            send.setCreate(new Timestamp(System.currentTimeMillis()));
            if (mail.getSubject().equals("")) mail.setSubject("无主题");
            if (Util.Blog2String(mail.getContent()).equals("")) mail.setContent(new SerialBlob(" ".getBytes("utf-8")));
            if (at != null) {
                send.setStatus(at);
            } else {
                Bind bind = bindDao.queryBy("mail", send.getFrom()).get(0);
                boolean bool = MailUtil.sendComplexMail(send.getFrom(), send.getTo(), mail.getSubject(), Util.Blog2String(mail.getContent()), attachments, bind.getAuth());
                if (bool)
                    send.setStatus("sended");
                else
                    throw new RuntimeException("发送失败");
            }
            saveMail(mail);
            saveSend(send);
            saveAttachment(mid, attachments);
        } catch (Exception e) {
            try {
                DBUtils.getConnection().rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            System.out.println("发送报错"+e.getMessage());
            return -2;
        }
        try {
            DBUtils.getConnection().commit();
            DBUtils.getConnection().setAutoCommit(true);
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }
    public List<Send> getAlready(String uid){
        String[] keys = {"uid","status"};
        String[] vals = {uid,"sended"};
        return sendDao.queryBy(keys,vals);
    }
    public Mail getMail(String mid){
        return mailDao.queryByMid(mid);
    }
    public int deleteAttachment(String aid){
        return attachmentDao.deleteAttachmentByRid(aid);
    }
    public int saveDraft(Mail mail, Draft draft, String[] attachments, boolean insert) {
        String mid = UUID.randomUUID().toString();
        if (mail.getMid()!=""&&mail.getMid()!=null){
            mid = mail.getMid();
        }
        mail.setMid(mid);
        draft.setDid(UUID.randomUUID().toString());
        draft.setMid(mid);
        draft.setFrom(draft.getFrom().trim());
        draft.setTo(draft.getTo().trim());
        draft.setCreate(new Date(System.currentTimeMillis()));
        if (attachments!=null){
            saveAttachment(mid,attachments);
        }
        mailDao.save(mail);
        draftDao.save(draft);
        return 1;
    }
    public List<Draft> getDraft(String uid) {
        return draftDao.query("uid",uid);
    }
    public int deleteDraft(String uid, String mid) {
        Draft draft = draftDao.query("mid", mid).get(0);
        draftDao.deleteBy("mid",mid);
        Recycle recycle = new Recycle();
        recycle.setCreate(new Date(System.currentTimeMillis()));
        recycle.setRid(UUID.randomUUID().toString());
        recycle.setMid(mid);
        recycle.setUid(uid);
        recycle.setFrom(draft.getFrom());
        recycle.setTo(draft.getTo());
        recycle.setSubject(draft.getSubject());
        recycleDao.save(recycle);
       return 1;
    }
    public int deleteAlready(String uid, String mid) {
        Recycle recycle = new Recycle();
        recycle.setCreate(new Date(System.currentTimeMillis()));
        recycle.setRid(UUID.randomUUID().toString());
        recycle.setMid(mid);
        recycle.setUid(uid);
        String[] key={"uid","mid"};
        String[] val = {uid,mid};
        Send send = sendDao.queryBy(key,val).get(0);
        recycle.setFrom(send.getFrom());
        recycle.setTo(send.getTo());
        recycle.setSubject(send.getSubject());
        sendDao.deleteBy("mid",mid);
        recycleDao.save(recycle);
        return 1;
    }
    public int deleteRecycle(String mid) {
        recycleDao.deleteBy("mid",mid);
        mailDao.deleteBy("mid",mid);
        return 1;
    }
    public List<Recycle> getRecycle(String uid) {
       return recycleDao.query("uid",uid);
    }
    public int clear(String uid) {
        return   recycleDao.deleteBy("uid",uid);
    }

    public int saveAttachment(String mid, String[] path){
        int count = 0;
        if (path!=null) {
            for (int i = 0; i < path.length; i++) {
                System.out.println(path[i]);
                Attachment attachment = new Attachment();
                attachment.setAid(UUID.randomUUID().toString());
                attachment.setMid(mid);
                attachment.setPath(path[i]);
                count = attachmentDao.save(attachment);
            }
        }
        return count;
    }
    public List<Attachment> getAttachment(String mid){
        return attachmentDao.getAttachmentByMid(mid);
    }
}
