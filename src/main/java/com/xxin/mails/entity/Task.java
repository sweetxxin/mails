package com.xxin.mails.entity;

import java.sql.Timestamp;

public class Task {
    private String tid;
    private String sid;
    private String status;
    private Timestamp create;

    public Timestamp getAt() {
        return at;
    }

    public void setAt(Timestamp at) {
        this.at = at;
    }

    private Timestamp at;
    public Task() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }
}
