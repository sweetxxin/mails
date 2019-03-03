package com.xxin.mails.service;



import com.xxin.mails.conf.Constants;
import com.xxin.mails.dao.AttachmentDao;
import com.xxin.mails.dao.BindDao;
import com.xxin.mails.dao.MailDao;
import com.xxin.mails.dao.ReceiveDao;
import com.xxin.mails.entity.Attachment;
import com.xxin.mails.entity.Bind;
import com.xxin.mails.entity.Mail;
import com.xxin.mails.entity.Receive;
import com.xxin.mails.utils.*;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReceiveService {
    private BindDao bindDao = new BindDao();
    private MailDao mailDao = new MailDao();
    private ReceiveDao receiveDao = new ReceiveDao();
    private AttachmentDao attachmentDao = new AttachmentDao();

    public ResultUtil getNewMail(String uid){
        ResultUtil resultUtil = new ResultUtil();
        String[] key = {"uid"};
        String[] val = {uid};
        List<Bind> binds = null;
        if (uid!=null){
            binds =   bindDao.query(key, val);
        }
        List<Receive> receives = new ArrayList<>();
        int count = 0;
        if (binds!=null) {
            for (Bind b : binds) {
                Message[] messages = getNewMessage(b.getMail(), b.getAuth(),"imap");
                count += messages.length;
                if (messages.length<=0){
                    List<Receive> re = getFromDB(b.getMail(),"unSee");
                    if (re!=null){
                        receives.addAll(re);
                    }
                    continue;
                }
                ReceiveOneMail pmm = null;
                for (int i = 0; i < messages.length; i++) {
                    pmm = new ReceiveOneMail((MimeMessage) messages[i]);
                    Receive r = null;
                    try {
                        if (pmm.getMessageId()!=null){
                            r = receiveDao.queryByRid(pmm.getMessageId());
                        }
                        if (r==null&&pmm.getFrom()!=null){
                            System.out.println("****************************有未读***********************");
                           try {
                               DBUtils.getConnection().setAutoCommit(false);
                               pmm.getMailContent((Part) messages[i]);
                               String content = pmm.getBodyText();
                               Mail mail = new Mail();
                               String mid = UUID.randomUUID().toString();
                               mail.setMid(mid);
                               mail.setSubject(pmm.getSubject());
                               pmm.getMailContent((Part) messages[i]);
                               mail.setContent(Util.String2Blog(content));
                               mailDao.save(mail);
                               Receive receive = new Receive();
                               receive.setMid(mid);
                               receive.setUid(uid);
                               receive.setRid(pmm.getMessageId());
                               receive.setCreate(new Timestamp(pmm.getSentDate().getTime()));
                               receive.setStatus("unSee");
                               receive.setFrom(pmm.getFrom());
                               receive.setTo(pmm.getMailAddress("to"));
                               receive.setSubject(pmm.getSubject());
                               receiveDao.save(receive);
                               receives.add(receive);
                               System.out.println("Message " + i + "  containAttachment: " + pmm.isContainAttach((Part) messages[i]));
                               if (pmm.isContainAttach((Part) messages[i])) {
                                   File file = new File(this.getClass().getResource("/").getPath());
                                   Date d = new Date();
                                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                   String date = sdf.format(d);
                                   File f = new File(file.getParent());
                                   pmm.setAttachPath(f.getParent()+ "/static/attachment/receive/"+date+"/");
                                   pmm.saveAttachMent((Part) messages[i]);
                                   Attachment attachment = new Attachment();
                                   attachment.setPath(Constants.STATIC_RESOURCES_PATH+"/attachment/receive/"+date+"/"+Util.getFileNameFromUrl(pmm.getFileName().replace("\\","/" )));
                                   System.out.println("附件url:"+attachment.getPath());
                                   attachment.setAid(UUID.randomUUID().toString());
                                   attachment.setMid(mid);
                                   attachmentDao.save(attachment);
                               }
                               DBUtils.getConnection().commit();
                               DBUtils.getConnection().setAutoCommit(true);
                               System.out.println(pmm.getBodyText());
                               System.out.println("保存新接收邮件");
                           } catch (SQLException e) {
                               dealErrorMail(messages[i]);
                               continue;
                           } catch (MessagingException e) {
                               dealErrorMail(messages[i]);
                               continue;
                           } catch (Exception e) {
                               dealErrorMail(messages[i]);
                               continue;
                           }
                        }else {
                            if (r!=null)
                                receives.add(r);
                        }
                    } catch (MessagingException e) {
                       dealErrorMail(messages[i]);
                    }
                }
            }
            resultUtil.setData(receives);
        }
        resultUtil.setCode(count);
        return resultUtil;
    }
    public void dealErrorMail(Message message){
        try {
            message.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException e1) {
            e1.printStackTrace();
        }
        try {
            DBUtils.getConnection().rollback();
        } catch (SQLException e) {
            System.out.println("报错"+e.getMessage());
        }
    }
    private List<Receive> getFromDB(String mail, String flag) {
        String[] key = {"`to`","status"};
        String[] val = {mail,flag};
       return  receiveDao.query(key,val);
    }
    public static  Message[] getNewMessage(String addr, String auth, String protocol){
        Message messages[] = {};
        Folder folder = null;
        try {
            folder =  MailUtil.connect(addr,auth,protocol);
            folder.open(Folder.READ_WRITE);
            int n = folder.getUnreadMessageCount();// 得到未读数量
            System.out.println("unSee" + n);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // false代表未读，true代表已读
            messages= folder.search(ft);
            System.out.println("Messages's length: " + messages.length);
            return messages;
        }catch (Exception e){
            System.out.println("报错"+e.getMessage());
        }
        return messages;
    }
    public void setMailFlag(final String id, final String flag, final String addr, final String auth){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Folder folder = MailUtil.connect(addr,auth,"imap");
                try {
                    folder.open(Folder.READ_WRITE);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                Message[] messages = new Message[0];
                try {
                    messages = folder.getMessages();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                for (Message message:messages) {
                    try {
                        System.out.println("开始修改标记查询");
                        ReceiveOneMail pmm = new ReceiveOneMail((MimeMessage) message);
                        if (pmm.getMessageId() != null && pmm.getMessageId().equals(id)) {
                            switch (flag) {
                                case "seen":
                                    message.setFlag(Flags.Flag.SEEN, true);
                                    System.out.println("标记邮件");
                                    break;
                                case "delete":
                                    message.setFlag(Flags.Flag.DELETED, true);
                                    Flags flags = message.getFlags();
                                    System.out.println("删除邮件");
                                    break;
                                default:     System.out.println("修改邮件状态失败");
                            }
                            break;
                        }
                    }catch (Exception e){
                        try {
                            message.setFlag(Flags.Flag.DELETED, true);
                        } catch (MessagingException e1) {
                            e1.printStackTrace();
                        }
                        continue;
                    }
                }
                if (folder!=null){
                    try {
                        folder.close(true);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void setReceiveFlag(String mid, String flag) {
        try {
            Receive receive = receiveDao.queryByMid(mid).get(0);
            String rid = receive.getRid();
            String to = receive.getTo();
            String auth = bindDao.queryBy("mail", to).get(0).getAuth();
            setMailFlag(rid, flag,to,auth);
            if (receive.getStatus().equals("unSee")){
                receive.setStatus(flag);
                receiveDao.save(receive);
            }
            System.out.println("从服务器上修改邮件状态"+flag+rid);
        }catch (Exception e){
            System.out.printf("error at ReceiveService.setReceiveFlag 228:"+e.getMessage());
        }

    }
    //   public static  Message[] getNewPop3Message(String addr,String auth)throws MessagingException {
//        Properties props = System.getProperties();
//        props.put("mail.smtp.host", MailUtil.chooseServer(addr));
//        props.put("mail.smtp.auth", "true");
//        //ssl加密
//        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.setProperty("mail.pop3.socketFactory.fallback", "true");
//        props.setProperty("mail.pop3.socketFactory.port", "995");
//        Session session = Session.getDefaultInstance(props, null);
//        session.setDebug(true);
//        URLName urln = new URLName("pop3", Util.chooseHost(addr,"pop3"), 110, null,
//                addr, auth);
//        Store store = session.getStore(urln);
//        store.connect();
//        Folder folder = store.getFolder("INBOX");
//        folder.open(Folder.READ_ONLY);
    //     Message messages[] = {};
//        Folder folder = null;
//        try {
//            folder =  MailUtil.connect(addr,auth,"pop3");
//            folder.open(Folder.READ_WRITE);
//            int n = folder.getUnreadMessageCount();// 得到未读数量
//            System.out.println("unSee" + n);
//            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // false代表未读，true代表已读
//            messages= folder.search(ft);
//            System.out.println("Messages's length: " + messages.length);
//            return messages;
//        }catch (Exception e){
//            System.out.println("报错"+e.getMessage());
//        }
//        return messages;
//    }
//    public static  Message[] getNewImapMessage(String addr,String auth){
//        Message messages[] = {};
//        Folder folder = null;
//        try {
//            folder =  MailUtil.connect(addr,auth,"imap");
//            folder.open(Folder.READ_WRITE);
//            int n = folder.getUnreadMessageCount();// 得到未读数量
//            System.out.println("unSee" + n);
//            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // false代表未读，true代表已读
//            messages= folder.search(ft);
//            System.out.println("Messages's length: " + messages.length);
//            return messages;
//        }catch (Exception e){
//            System.out.println("报错"+e.getMessage());
//        }
//        return messages;
//    }
}
