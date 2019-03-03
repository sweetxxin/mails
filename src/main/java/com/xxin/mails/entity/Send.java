package com.xxin.mails.entity;

import java.sql.Timestamp;

public class Send {
    private String uid;
    private String sid;
    private String mid;
    private Timestamp create;
    private String status;
    private String from;
    private String to;
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public Send() {
    }

    @Override
    public String toString() {
        return "Send{" +
                "uid='" + uid + '\'' +
                ", sid='" + sid + '\'' +
                ", mid='" + mid + '\'' +
                ", create=" + create +
                ", status='" + status + '\'' +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Timestamp getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
