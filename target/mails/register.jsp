<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("loginStatus")!=null&&session.getAttribute("loginStatus").equals("already")){
        session.removeAttribute("loginStatus");
    }
%>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../../../favicon.ico">

    <title>Register</title>
    <script src="./static/js/frame.js"></script>
    <link href="./static/css/signin.css" rel="stylesheet">
      <style>
          .password{
              left: 69px;
              width: 233px;
          }
          .password-span{
              display: inline-block;
              background-color: #eee;
              left: 0px;
              height: auto;
              width: auto;
              position: absolute;
              padding: 14px 12px;
          }
      </style>
  </head>

   <body class="text-center">
       <div id="show"  style="display:none;position: absolute;top: 50px;z-index: 100" class="alert alert-warning alert-dismissible" role="alert">
          <button type="button" id="hide" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <strong>Warning!</strong>
          <p id="msg">Better check yourself, you're not looking too good.</p>
        </div>
     <form class="form-signin">
       <h1 class="h3 mb-3 font-weight-normal">Please Register</h1>
         <div class="input-group" style="margin-bottom: 13px;">
             <span class="input-group-addon" >用户名</span>
             <input type="text" id="username" class="form-control" placeholder="Username" aria-describedby="sizing-addon2" required>
         </div>
         <div class="input-group group">
             <span class="input-group-addon password-span" >&nbsp;&nbsp;密码&nbsp;&nbsp;</span>
             <input type="password" id="password" style="width: 233px" class="form-control password" placeholder="Password" aria-describedby="sizing-addon2">
         </div>
         <div class="input-group group">
             <span class="input-group-addon password-span" >确认密码</span>
             <input type="password" id="confirm" style="width: 233px" class="form-control password" placeholder="Confirm Password" aria-describedby="sizing-addon2">
         </div>
         <div class="input-group group" style="margin-bottom: 13px;">
             <span class="input-group-addon" >邮箱</span>
             <input type="text" id="mail" class="form-control" placeholder="Your Email" aria-describedby="sizing-addon2" required>
         </div>
         <div class="input-group group" style="margin-bottom: 13px;">
             <span class="input-group-addon">姓名</span>
             <input type="text" id="name" class="form-control" placeholder="Your Name" aria-describedby="sizing-addon2">
         </div>
         <div class="input-group group" style="margin-bottom: 13px;">
             <span class="input-group-addon">手机号</span>
             <input type="text" id="telephone" class="form-control" placeholder="Telephone" aria-describedby="sizing-addon2">
         </div>
       <button class="btn btn-lg btn-primary btn-block" style="margin-bottom: 13px;" id="register" type="button">Register</button>
        <a href="index.jsp">已有账号？直接登陆go</a>
     </form>
   </body>
 </html>
 <script>
     $('#username').blur(function () {
         if ( $('#username').val()!="")
            doRegister();
     })
    $('#password').blur(function () {
        if(!checkPassWord($('#password').val())){
            tip("密码格式有误(包含数字字母，至少6位)");
        }else {
            $("#tip").remove();
        }
    })
    $('#mail').blur(function () {
        if (!checkMail($('#mail').val())) {
            tip("邮箱格式错误")
        }else {
            $("#tip").remove();
        }
    })
     $('#telephone').blur(function () {
         if (!checkMobile($('#telephone').val())) {
            tip("手机格式错误")
         }else {
             $("#tip").remove();
         }
     })
     $('#confirm').blur(function () {
         if (!($('#password').val().length>0&&$('#password').val()== $('#confirm').val())){
             tip("两次密码不一致")
         }else {
             $("#tip").remove();
         }
     })
    $('#register').click(function () {
        doRegister();
    })
     function doRegister() {
         var user = {
             "username": $('#username').val(),
             "password": $('#password').val(),
             "mail"    : $('#mail').val(),
             "telephone":$('#telephone').val(),
             "name"    : $('#name').val()
         };
         $.post(
             "./user/register",
             {
                user: JSON.stringify(user)
             },
             function (res) {
                 res = JSON.parse(res)
                 if(res.code==200){
                     if(confirm("注册成功，马上登陆?")){
                         $.cookie("username",$('#username').val());
                         $.cookie("password",$('#password').val());
                         window.location="index.jsp";
                     }
                 }else{
                     tip(res.msg)
                 }
             }
         )
     }
     function checkPassWord(password) {//密码必须包含数字和字母
         var str = password;
         if (str == null || str.length < 6) {
             return false;
         }
         var reg = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
         if (reg.test(str))
             return true;
     }
     function checkMobile(sMobile){
         if(!(/^1[3|4|5|8][0-9]\d{4,8}$/.test(sMobile))){
             return false;
         }else{
             return true;
         }
     }
     function checkMail(mail) {
         var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
         if (filter.test(mail)) return true;
         else {
             return false;}
     }
     function tip(s) {
         $("#tip").remove();
         $("body").append('' +
             '<div id="tip" style="position: absolute;top: 0%;z-index: 100;" class="alert alert-dismissible alert-warning " role="alert">' +
             '  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
             '  <strong>Tip!</strong>' +
             '  <p">'+s+'</p>' +
             '</div>');
     }

 </script>