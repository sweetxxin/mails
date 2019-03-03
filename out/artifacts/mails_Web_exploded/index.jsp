<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  if (session.getAttribute("loginStatus")!=null&&session.getAttribute("loginStatus").equals("already")){
    Cookie cookie = new Cookie("uid", session.getAttribute("loginUser").toString());
    response.addCookie(cookie);
    response.sendRedirect("main.jsp");
}
%>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <link rel="icon" href="../../../../favicon.ico">


  <title>SignIn</title>
  <script src="static/js/frame.js"></script>
  <!-- Custom styles for this template -->
  <link href="static/css/signin.css" rel="stylesheet">
  <style>
    .group{
      margin-top: 13px;
    }
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
    #tip-draggable{
      position:absolute;
      top:30%;
      left:50%;
      transform:translateX(-50%) translateY(-50%);
      -moz-transform:translateX(-50%) translateY(-50%);
      -webkit-transform:translateX(-50%) translateY(-50%);
      -ms-transform:translateX(-50%) translateY(-50%);
      font-size:9pt;
      width:30%;
      z-index: 2000;
      display: none;
    }
  </style>
</head>

<body class="text-center">

<form class="form-signin">
  <h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
  <div class="input-group group">
    <span class="input-group-addon" >用户名</span>
    <input type="text" id="username" class="form-control" placeholder="Username" aria-describedby="sizing-addon2">
  </div>
  <div class="input-group group">
    <span class="input-group-addon password-span" >&nbsp;&nbsp;密码&nbsp;&nbsp;</span>
    <input type="password" id="password" style="width: 233px" class="form-control password" placeholder="Password" aria-describedby="sizing-addon2">
  </div>
  <div class="checkbox mb-3">
    <label>
      <input type="checkbox" id="remember" value="remember-me"> Remember me
    </label>
  </div>
  <a href="register.jsp">未注册？go</a>
  <br>
  <button class="btn btn-lg btn-primary btn-block" id="signIn" type="button">Sign in</button>
  <p class="mt-5 mb-3 text-muted">&copy; 2018-2019</p>
</form>

<div id="tip-draggable">
  <div class="panel panel-primary" style="position:absolute;width: 100%;">
    <div class="panel-heading">
      <h3 class="panel-title">提示</h3>
    </div>
    <div class="panel-body ">
      <p id="tip">自动登陆中...</p>
      <div style="margin-top: 1%;">
        <button id="sure" style="margin-right: 1%" type="button" class="btn btn-success">确定</button>
        <button type="button" id="cancel" class="btn btn-danger">取消</button>
      </div>
    </div>
  </div>
</div>


</body>
</html>
<script src="static/js/common.js"></script>
<script>
  var interval;
    $(document).ready(function () {
      dragFunc('tip-draggable');
     localStorage.setItem("bind","");
     localStorage.setItem("receives","");
      $.cookie("uid", "");
      if ($.cookie("username") != "" && $.cookie("username")!=null) {
        $("#username").val($.cookie("username"));
        $("#password").val($.cookie("password"));
        $('#remember').attr("checked", true);
        var flag = 1;
        setTimeout(function () {
          $("#tip-draggable").show();
          var i = 5;
          interval=self.setInterval(function () {
              $('#sure').html("确定"+"("+i+"s)");
              i--;
              if (i<=0){
                window.clearInterval(interval);
                login();
              }
            },1000)
        }, 500)
      }
    })
  $('#sure').click(function () {
      window.clearInterval(interval);
      $('#tip').html("正在登陆中...");
      login();
  })
   $('#cancel').click(function () {
     window.clearInterval(interval);
     $('#tip-draggable').hide()
   })
  $("#signIn").click(function () {
      if ($('#remember').is(':checked')) {
          $.cookie("username",$("#username").val());
          $.cookie("password",$("#password").val());
      }else {
          $.cookie("username","");
          $.cookie("password","");
      }
      login();
  })
  function login() {
      $.post("./user/login",
          {
              username : $("#username").val(),
              password : $("#password").val()
          },
          function (res) {
            res = JSON.parse(res);
            if (res.code!=200){
                $("#tip").remove();
                $("body").append('' +
                    '<div id="tip" style="position: absolute;top: 2%;z-index: 100;" class="alert alert-dismissible alert-warning " role="alert">' +
                    '  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                    '  <strong>Tip!</strong>' +
                    '  <p">'+res.msg+'</p>' +
                    '</div>');
            }else{
                $.cookie("uid",res.data);
               window.location="main.jsp?page=my";
            }
          }
      )
  }
</script>
