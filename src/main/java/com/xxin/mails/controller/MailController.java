package com.xxin.mails.controller;

import com.xxin.mails.conf.Constants;
import com.xxin.mails.entity.*;
import com.xxin.mails.service.ReceiveService;
import com.xxin.mails.service.RequestService;
import com.xxin.mails.service.SendService;
import com.xxin.mails.utils.JsonUtil;
import com.xxin.mails.utils.ResultUtil;
import com.xxin.mails.utils.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name="mail", urlPatterns={"/mail/*"})
public class MailController extends HttpServlet {
    private SendService sendService = new SendService();
    private ReceiveService receiveService = new ReceiveService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestService.dispatch(this, request, response);
        //      String s = request.getPathInfo();
//      s = s.replace("/","");
//        try {
//            response.getWriter().write(this.getClass().getDeclaredMethod(s,HttpServletRequest.class).invoke(this,request).toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public String send(HttpServletRequest request, HttpServletResponse response){
        ResultUtil<String> resultUtil = new ResultUtil<>();
        Mail mail = JsonUtil.jsonToPojo(request.getParameter("mail"),Mail.class);
        Send send = JsonUtil.jsonToPojo(request.getParameter("send"),Send.class);
        int res;
        try {
            mail.setContent(new SerialBlob(Util.String2Blog(request.getParameter("blob"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (request.getParameter("attachment")!=null&&request.getParameter("attachment")!=""){
            String[] attachment = setAttachmentPath(request);
            System.out.println("有附件");
            res = sendService.send(mail,send,attachment,request.getParameter("at"));
        }else {
            System.out.println("无附件");
            res = sendService.send(mail,send,null,request.getParameter("at"));
        }
        resultUtil.setMsg("发送成功");
        if (request.getParameter("at")!=null){
            resultUtil.setMsg("设置成功");
        }
        resultUtil.setCode(Constants.RESPONSE_OK);
        if (res!=1){
            resultUtil.setCode(Constants.RESPONSE_FAIL);
            resultUtil.setMsg("发送失败");
        }
        return JsonUtil.objectToJson(resultUtil);
    }
    public String already(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        List<Send>sends = sendService.getAlready(request.getParameter("uid"));
        resultUtil.setCode(Constants.RESPONSE_OK);
        resultUtil.setMsg("获取成功");
        resultUtil.setData(sends);
        return JsonUtil.objectToJson(resultUtil);
    }
    public String mail(HttpServletRequest request, HttpServletResponse response){
        ResultUtil<HashMap<String,Object>> resultUtil = new ResultUtil();
        Mail mail = sendService.getMail(request.getParameter("mid"));
        if (mail!=null){
            resultUtil.setCode(Constants.RESPONSE_OK);
            resultUtil.setMsg("获取成功");
            HashMap map = new HashMap();
            String content =  Util.Blog2String(mail.getContent());
            content = content.replace(request.getSession().getServletContext().getRealPath("/WEB-INF").replace("\\","/"),"");
            System.out.println(content);
            map.put("content",content);
            mail.setContent(null);
            map.put("mail", mail);
            List<Attachment>attachments =sendService.getAttachment(mail.getMid());
            map.put("attachment", attachments);
            resultUtil.setData(map);
        }else {
            resultUtil.setCode(Constants.RESPONSE_FAIL);
            resultUtil.setMsg("获取失败");
        }
        return JsonUtil.objectToJson(resultUtil);
    }
    public String saveDraft(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        Mail mail = JsonUtil.jsonToPojo(request.getParameter("mail"),Mail.class);
        Draft draft = JsonUtil.jsonToPojo(request.getParameter("draft"),Draft.class);
        mail.setContent(Util.String2Blog(request.getParameter("blob")));
        String[] attachment = null;
        if (request.getParameter("attachment")!=null&&request.getParameter("attachment")!="") {
            attachment = setAttachmentPath(request);
        }
        sendService.saveDraft(mail, draft,attachment, request.getParameter("insert").equals("true")?true:false);
        resultUtil.setData("保存成功");
        resultUtil.setCode(Constants.RESPONSE_OK);
        return JsonUtil.objectToJson(resultUtil);
    }
    public String getDraft(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        List<Draft> drafts = sendService.getDraft(request.getParameter("uid"));
        resultUtil.setCode(Constants.RESPONSE_OK);
        resultUtil.setMsg("获取成功");
        resultUtil.setData(drafts);
        return JsonUtil.objectToJson(resultUtil);
    }
    public String getReceive(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        try {
            resultUtil = receiveService.getNewMail(request.getParameter("uid"));
        } catch (Exception e) {
            resultUtil.setCode(Constants.RESPONSE_FAIL);
            e.printStackTrace();
        }
       return JsonUtil.objectToJson(resultUtil);
    }
    public String delete(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        resultUtil.setCode(Constants.RESPONSE_OK);
        resultUtil.setMsg("删除成功");
        if (request.getParameter("type").equals("draft")){
            sendService.deleteDraft(request.getParameter("uid"),request.getParameter("mid"));
        }else if (request.getParameter("type").equals("already")){
            sendService.deleteAlready(request.getParameter("uid"),request.getParameter("mid"));
        }else if (request.getParameter("type").equals("recycle")){
            sendService.deleteRecycle(request.getParameter("mid"));
        }else if (request.getParameter("type").equals("receive")){
            receiveService.setReceiveFlag(request.getParameter("mid"), "delete");
        }else {
            resultUtil.setCode(Constants.RESPONSE_FAIL);
            resultUtil.setMsg("删除出错");
        }
        return JsonUtil.objectToJson(resultUtil);
    }
    public String recycle(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        List<Recycle>recycles = sendService.getRecycle(request.getParameter("uid"));
        resultUtil.setCode(Constants.RESPONSE_OK);
        resultUtil.setMsg("获取成功");
        resultUtil.setData(recycles);
        return JsonUtil.objectToJson(resultUtil);
    }
    public String clear(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        sendService.clear(request.getParameter("uid"));
        resultUtil.setCode(Constants.RESPONSE_OK);
        resultUtil.setMsg("清空成功");
        return JsonUtil.objectToJson(resultUtil);
    }
    public String seen(HttpServletRequest request, HttpServletResponse response){
        ResultUtil resultUtil = new ResultUtil();
        resultUtil.setCode(Constants.RESPONSE_OK);
        receiveService.setReceiveFlag(request.getParameter("mid"),"seen");
        return JsonUtil.objectToJson(resultUtil);
    }
    private String[] setAttachmentPath(HttpServletRequest request){
        String[] attachment = request.getParameter("attachment").split(",");
        if (attachment.length==0){
            attachment[0] = request.getParameter("attachment");
        }
        return attachment;
    }
}
