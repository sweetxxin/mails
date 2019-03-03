package com.xxin.mails.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * 有一封邮件就需要建立一个ReciveMail对象
 */
public class ReceiveOneMail {
    private MimeMessage mimeMessage = null;
    private String saveAttachPath = ""; //附件下载后的存放目录
    private StringBuffer bodytext = new StringBuffer();//存放邮件内容
    private String dateformat = "yy-MM-dd HH:mm"; //默认的日前显示格式
    private String fileName = "";

    public ReceiveOneMail(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    public void setMimeMessage(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    /**
     * 获得发件人的地址和姓名
     */
    public String getFrom() throws MessagingException {
            InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
            String from = address[0].getAddress();
            if (from == null)
                from = "";
            return from;
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
     */
    public String getMailAddress(String type) throws Exception {
        String mailaddr = "";
        String addtype = type.toUpperCase();
        InternetAddress[] address = null;
        if (addtype.equals("TO") || addtype.equals("CC")|| addtype.equals("BCC")) {
            if (addtype.equals("TO")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
            } else if (addtype.equals("CC")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
            }
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String email = address[i].getAddress();
                    if (email == null)
                        email = "";
                    else {
                        email = MimeUtility.decodeText(email);
                    }
                    String compositeto =  email;
                    mailaddr += "," + compositeto;
                }
                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new Exception("Error emailaddr type!");
        }
        return mailaddr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获得邮件主题
     */
    public String getSubject() throws MessagingException {
        String subject = "";
        try {
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            if (subject == null)
                subject = "";
        } catch (Exception exce) {}
        return subject;
    }

    /**
     * 获得邮件发送日期
     */
    public Date getSentDate() throws Exception {
        Date sentdate = mimeMessage.getSentDate();
        return sentdate;
    }

    /**
     * 获得邮件正文内容
     */
    public String getBodyText() {
        return bodytext.toString();
    }

    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     */
    public void getMailContent(Part part) throws MessagingException, IOException {
            String contenttype = part.getContentType();
            int nameindex = contenttype.indexOf("name");
            boolean conname = false;
            if (nameindex != -1)
                conname = true;
            System.out.println("CONTENTTYPE: " + contenttype);
            if (part.isMimeType("text/plain") && !conname) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("text/html") && !conname) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    getMailContent(multipart.getBodyPart(i));
                }
            } else if (part.isMimeType("message/rfc822")) {
                getMailContent((Part) part.getContent());
            } else {
                bodytext.append(" ");
            }

    }

    /**
     * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
     */
    public boolean getReplySign() {
        boolean replysign = false;
        try {
            String needreply[] = mimeMessage
                    .getHeader("Disposition-Notification-To");
            if (needreply != null) {
                replysign = true;
            }
        }catch (Exception e){
            System.out.println("报错"+e.getMessage());
        }
        return replysign;
    }

    /**
     * 获得此邮件的Message-ID
     */
    public String getMessageId() throws MessagingException {
        return mimeMessage.getMessageID();
    }

    /**
     * 判断此邮件是否包含附件
     */
    public boolean isContainAttach(Part part) throws MessagingException, IOException {
        boolean attachflag = false;
            String contentType = part.getContentType();
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if ((disposition != null)
                            && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                            .equals(Part.INLINE))))
                        attachflag = true;
                    else if (mpart.isMimeType("multipart/*")) {
                        attachflag = isContainAttach((Part) mpart);
                    } else {
                        String contype = mpart.getContentType();
                        if (contype.toLowerCase().indexOf("application") != -1)
                            attachflag = true;
                        if (contype.toLowerCase().indexOf("name") != -1)
                            attachflag = true;
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                attachflag = isContainAttach((Part) part.getContent());
            }
        return attachflag;
    }

    /**
     * 【保存附件】
     */
    public void saveAttachMent(Part part) {
        String fileName = "";
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if ((disposition != null)
                            && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                            .equals(Part.INLINE)))) {
                        fileName = mpart.getFileName();
                        if (fileName.toLowerCase().indexOf("gb2312") != -1) {
                            fileName = MimeUtility.decodeText(fileName);
                        }
                        saveFile(fileName, mpart.getInputStream());
                    } else if (mpart.isMimeType("multipart/*")) {
                        saveAttachMent(mpart);
                    } else {
                        fileName = mpart.getFileName();
                        if ((fileName != null)
                                && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
                            fileName = MimeUtility.decodeText(fileName);
                            saveFile(fileName, mpart.getInputStream());
                        }
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                saveAttachMent((Part) part.getContent());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("报存了");
    }

    /**
     * 【设置附件存放路径】
     */

    public void setAttachPath(String attachpath) {
        this.saveAttachPath = attachpath;
    }

    /**
     * 【设置日期显示格式】
     */
    public void setDateFormat(String format) throws Exception {
        this.dateformat = format;
    }

    /**
     * 【获得附件存放路径】
     */
    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * 【真正的保存附件到指定目录里】
     */
    private void saveFile(String fileName, InputStream in) throws Exception {

        File path = new File(getAttachPath());
        if (!path.exists()){
            path.mkdirs();
        }
        File storefile = new File(path,fileName);
        System.out.println("storefile's path: " + storefile.toString());
        setFileName(storefile.toString());
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("文件保存失败!");
        } finally {
            bos.close();
            bis.close();
        }
    }

    /**
     * PraseMimeMessage类测试
     */
    public static void main(String args[]) throws Exception {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.sina.com");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, null);
        URLName urln = new URLName("pop3", "pop3.sina.com", 110, null,
                "sweetxxin@sina.com", "18926278337");
        Store store = session.getStore(urln);
        store.connect();
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message message[] = folder.getMessages();
        System.out.println("Messages's length: " + message.length);
        ReceiveOneMail pmm = null;
        for (int i = 0; i < message.length; i++) {
            System.out.println("======================");
            pmm = new ReceiveOneMail((MimeMessage) message[i]);
            System.out.println("Message " + i + " subject: " + pmm.getSubject());
            System.out.println("Message " + i + " sentdate: "+ pmm.getSentDate());
            System.out.println("Message " + i + " replysign: "+ pmm.getReplySign());
            System.out.println("Message " + i + "  containAttachment: "+ pmm.isContainAttach((Part) message[i]));
            System.out.println("Message " + i + " form: " + pmm.getFrom());
            System.out.println("Message " + i + " to: "+ pmm.getMailAddress("to"));
            System.out.println("Message " + i + " cc: "+ pmm.getMailAddress("cc"));
            System.out.println("Message " + i + " bcc: "+ pmm.getMailAddress("bcc"));
            pmm.setDateFormat("yy年MM月dd日 HH:mm");
            System.out.println("Message " + i + " sentdate: "+ pmm.getSentDate());
            System.out.println("Message " + i + " Message-ID: "+ pmm.getMessageId());
            // 获得邮件内容===============
            pmm.getMailContent((Part) message[i]);
            System.out.println("Message " + i + " bodycontent: \r\n"
                    + pmm.getBodyText());
            pmm.setAttachPath("c:\\");
            pmm.saveAttachMent((Part) message[i]);
        }
    }
}
