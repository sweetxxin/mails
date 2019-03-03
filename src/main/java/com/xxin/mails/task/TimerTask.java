package com.xxin.mails.task;

import com.xxin.mails.dao.TaskDao;
import com.xxin.mails.entity.Attachment;
import com.xxin.mails.entity.Mail;
import com.xxin.mails.entity.Send;
import com.xxin.mails.entity.Task;
import com.xxin.mails.service.SendService;
import com.xxin.mails.utils.DBUtils;
import com.xxin.mails.utils.Util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerTask {
    private List<Send> sends;
    public  void timerSend(){
        Runnable runnable = new Runnable() {
            public void run() {
                Object o = DBUtils.doSql("select * from send where status not in (?,?,?)",Send.class,"sended","sending","timing");
                if (o!=null&&((List<Send>) o).size()>0){
                    sends = (List<Send>) o;
                    final SendService sendService = new SendService();
                    for (final Send send:sends){
                        final Mail mail = sendService.getMail(send.getMid());
                        final Task task = new Task();
                        List<Attachment> attachments = sendService.getAttachment(send.getMid());
                        final String[] attach;
                        if (attachments!=null){
                            attach = new String[attachments.size()];
                            for (int i=0;i<attachments.size();i++){
                                attach[i] = attachments.get(0).getPath();
                            }
                        }else {
                            attach = null;
                        }
                        Date at = Util.string2Date(send.getStatus());
                        System.out.println("设置定时任务"+send+"at"+at);
                        if (at!=null){
                            send.setStatus("timing");
                            sendService.saveSend(send);
                            final TaskDao taskDao = new TaskDao();
                            task.setCreate(new Timestamp(System.currentTimeMillis()));
                            task.setSid(send.getSid());
                            task.setStatus("ready");
                            task.setAt(new Timestamp(at.getTime()));
                            task.setTid(UUID.randomUUID().toString());
                            taskDao.save(task);
                            Timer timer = new Timer();
                            timer .schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    System.out.println("执行定时");
                                    sendService.send(mail, send, attach, null);
                                    System.out.println("执行结束");
                                    task.setStatus("finished");
                                    taskDao.save(task);
                                }
                            }, at);
                        }
                    }
                }
            }
        };
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 10, 1, TimeUnit.SECONDS);
    }

}
