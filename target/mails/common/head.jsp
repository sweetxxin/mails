<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="./static/js/frame.js"></script>
</head>
<body>
<div class="page-header header">
    <h1>邮件代收发系统<small></small></h1>
    <div class="btn-group btn-div">
        <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            退出 <span class="caret"></span>
        </button>
        <ul class="dropdown-menu">
            <li ><a id="logout">注销</a></li>
            <li><a href="javascript:window.opener=null;window.open('','_self');window.close();">关闭</a></li>
            <li ><a href="../register.jsp">注册</a></li>
        </ul>
    </div>
</div>
</body>
</html>
<script>
    $('#logout').click(function () {
        $.post("./user/logout",{"uid":$.cookie("uid")},function (res) {
            window.location = "index.jsp";
        })
    });
</script>
