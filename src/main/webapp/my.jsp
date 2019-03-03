<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        #add{
            margin-left: 40%;
            margin-top: 1%;
        }
        #delete{
            position: absolute;
            top: 7%;
            right: 10px;
        }
    </style>
</head>
<body>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 class="panel-title">我的</h3>
    </div>
    <div class="panel-body">
        <div class="panel panel-default">
            <div class="panel-heading heading-bind">绑定/修改</div>
            <div class="panel-heading">已绑定邮箱</div>
            <div class="panel-body  bind-panel">
                <ul class="list-group">
                    <li class="list-group-item" style="padding: 0px;border: 0px">
                        <div class="input-group">
                            <span class="input-group-addon" >邮箱</span>
                            <input type="text" id="bindMail" class="form-control" placeholder="bind more...">
                        </div>
                        <div class="input-group">
                            <span class="input-group-addon" >授权码</span>
                            <input type="text" id="auth" class="form-control" placeholder="bind more...">
                        </div>
                        <button id="add" class="btn btn-primary" type="button">绑定/修改</button>
                    </li>
                </ul>
            </div>
            <div class="panel-body bind-panel">
                <ul class="list-group" id="bind-mail-ul">
                </ul>
            </div>
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">基本信息</div>
            <div class="panel-body">
                <ul class="list-group" id="info-ul">
                </ul>
                <button id="update" style="width:10%;margin-left: 45%" class="btn btn-primary" type="button">更新</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script>
    $(document).ready(function () {
        getUserInfo();
        localStorage.setItem("bind","");
    })

    $('#add').click(function () {
        $.post("./user/bind",
            {
                uid: $.cookie("uid"),
                mail: $('#bindMail').val(),
                auth:$('#auth').val()
            },
            function (res) {
                res = JSON.parse(res);
                if (res.code==200){
                    $('#bind-mail-ul').append('<li class="list-group-item">'+$('#bindMail').val()+' <button id="delete" onclick="deleteMail('+"'"+$('#bindMail').val()+"'"+')" class="btn btn-primary" type="button">删除</button></li>');
                    localStorage.setItem("bind",  (localStorage.getItem("bind")+","+$('#bindMail').val()));
                    $('#bindMail').val("");
                }
                alert(res.msg);
            }
        )
    })
    $('#update').click(function () {
        var user={"uid":$.cookie("uid"),
            "telephone":$('#telephone').val(),
            "name":$('#name').val(),
            "username":$('#username').val(),
            "mail":$('#mail').val(),
            "password":$.cookie("password")
        };
        $.post("./user/update",
            {
                user:JSON.stringify(user)
            },
            function (res) {
                alert(JSON.parse(res).msg)
            }
        )
    })
    function getUserInfo() {
        $.post("./user/info",
            {
                uid: $.cookie("uid"),
            },
            function (res) {
                res = JSON.parse(res);
                localStorage.setItem("bind",res.data.bind.join(","));
                $('#bind-mail-ul').empty();
                for (var i=0;i<res.data.bind.length;i++){
                    $('#bind-mail-ul').append('<li class="list-group-item">'+res.data.bind[i]+' <button id="delete" onclick="deleteMail('+"'"+res.data.bind[i]+"'"+')" class="btn btn-primary" type="button">删除</button></li>');
                }
                $('#info-ul').empty();
                $('#info-ul').append(
                    '<li class="list-group-item" style="padding: 0px;border: 0px">'+
                    '<div class="input-group">'+
                    ' <span class="input-group-addon" id="sizing-addon1">用户名</span>'+
                    ' <input type="text" id="username" class="form-control" value="'+res.data.user.username+'">'+
                    '</div> </li>'
                );
                $('#info-ul').append(
                    '<li class="list-group-item" style="padding: 0px;border: 0px">'+
                    '<div class="input-group">'+
                    ' <span class="input-group-addon" id="sizing-addon1">&nbsp;姓名&nbsp;</span>'+
                    ' <input type="text" id="name" class="form-control" value="'+res.data.user.name+'">'+
                    '</div> </li>'
                );
                $('#info-ul').append(
                    '<li class="list-group-item" style="padding: 0px;border: 0px">'+
                    '<div class="input-group">'+
                    ' <span class="input-group-addon" id="sizing-addon1">&nbsp;手机&nbsp;</span>'+
                    ' <input type="text" id="telephone" class="form-control" value="'+res.data.user.telephone+'">'+
                    '</div> </li>'
                );
                $('#info-ul').append(
                    '<li class="list-group-item" style="padding: 0px;border: 0px">'+
                    '<div class="input-group">'+
                    ' <span class="input-group-addon" id="sizing-addon1">&nbsp;邮箱&nbsp;</span>'+
                    ' <input type="text" id="mail" class="form-control" value="'+res.data.user.mail+'">'+
                    '</div> </li>'
                );
            }
        )
    }

  function deleteMail(d) {
      $.post("./user/remove",{"uid":$.cookie("uid"),"mail":d},function (res) {
        res = JSON.parse(res);
        if (res.code==200){
            alert(res.msg);
            getUserInfo();
        }
      })
  }
</script>

