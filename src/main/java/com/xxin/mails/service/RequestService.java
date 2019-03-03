package com.xxin.mails.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestService {
    public static void dispatch(Object obj, HttpServletRequest request, HttpServletResponse response){
        String s = request.getPathInfo();
        s = s.replace("/","");
        try {
            Object res = obj.getClass().getDeclaredMethod(s,HttpServletRequest.class,HttpServletResponse.class).invoke(obj,request,response);
            if (res!=null){
                response.getWriter().write(res.toString());
                response.getWriter().flush();
                response.getWriter().close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
