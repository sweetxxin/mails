package com.xxin.mails.dao;

import com.xxin.mails.entity.Task;
import com.xxin.mails.utils.DBUtils;

public class TaskDao {
    public int save(Task task){
        return DBUtils.save(task);
    }
}
