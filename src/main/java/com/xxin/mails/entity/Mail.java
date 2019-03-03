package com.xxin.mails.entity;


import javax.sql.rowset.serial.SerialBlob;

public class Mail {
    private String mid;
    private String subject;
    private SerialBlob content;

    public Mail() {
    }

    @Override
    public String toString() {
        return "Mail{" +
                "mid='" + mid + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public SerialBlob getContent() {
        return content;
    }

    public void setContent(SerialBlob content) {
        this.content = content;
    }
}
