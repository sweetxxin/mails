package com.xxin.mails.controller;

import com.jspsmart.upload.File;
import com.jspsmart.upload.SmartUpload;
import com.xxin.mails.conf.Constants;
import com.xxin.mails.service.RequestService;
import com.xxin.mails.service.SendService;
import com.xxin.mails.utils.JsonUtil;
import com.xxin.mails.utils.ResultUtil;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name="attachment", urlPatterns={"/attachment/*"})
public class AttachController extends HttpServlet {
    private SendService sendService = new SendService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestService.dispatch(this, request, response);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public String download(HttpServletRequest request, HttpServletResponse response){
        String path = request.getParameter("path").replace(Constants.STATIC_RESOURCES_PATH,request.getSession().getServletContext().getRealPath("/static").replace("\\", "/"));
        try {
            java.io.File f = new java.io.File(path);
            String filename= URLEncoder.encode(f.getName(),"utf-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition","attachment; filename="+filename+"");
            byte[] bytes =  FileUtils.readFileToByteArray(f);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String upload(HttpServletRequest request, HttpServletResponse response){
        SmartUpload su = new SmartUpload();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(d);
        String path = request.getSession().getServletContext().getRealPath("/static") + "/attachment/upload/" + date + "/";
        path = path.replace("\\","/");
        System.out.println(path);
        if (!(new java.io.File(path).exists())) {
            new java.io.File(path).mkdirs();
        }
        ResultUtil<String> resultUtil = new ResultUtil<>();
        String filename="";
        String res = "上传成功";
        try {
            su.initialize(getServletConfig(), request, response);
            su.upload();
            File smartFile = su.getFiles().getFile(0);
            filename =  System.currentTimeMillis()+smartFile.getFileName();
            su.getFiles().getFile(0).saveAs(path +filename);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("1015") ) {
                res = "上传失败:类型不正确";
            } else if (e.getMessage().contains("1105")) {
                res = "上传失败：总大小超过限制大小";
            } else if (e.getMessage().contains("1010")) {
                res = "上传失败：总大小超过限制大小";
            } else if (e.getMessage().contains("1110")) {
                res = "上传失败:总大小超过限制大小";
            }
            resultUtil.setCode(Constants.RESPONSE_FAIL);
        }
        resultUtil.setData(Constants.STATIC_RESOURCES_PATH+"/attachment/upload/" + date + "/"+filename);
        resultUtil.setMsg(res);
        resultUtil.setCode(Constants.RESPONSE_OK);
        return JsonUtil.objectToJson(resultUtil);
    }
    public String remove(HttpServletRequest request, HttpServletResponse response){
        String path = request.getParameter("path").replace(Constants.STATIC_RESOURCES_PATH,request.getSession().getServletContext().getRealPath("/static").replace("\\", "/"));
        java.io.File file = new java.io.File(path);
        ResultUtil resultUtil = new ResultUtil();
        if (file.isFile()){
             if(file.delete()){
                 if (request.getParameter("aid")!=null){
                     sendService.deleteAttachment(request.getParameter("aid"));
                 }
                 resultUtil.setMsg("删除成功");
                 resultUtil.setCode(Constants.RESPONSE_OK);
             }
        }else {
            resultUtil.setMsg("删除出错,找不到文件");
            resultUtil.setCode(Constants.RESPONSE_FAIL);
        }
       return JsonUtil.objectToJson(resultUtil);
    }
}
