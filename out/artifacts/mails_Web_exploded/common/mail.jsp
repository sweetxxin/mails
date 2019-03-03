<%@ page import="com.xxin.conf.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <%
        out.println("<link href="+"'"+ Constants.STATIC_RESOURCES_PATH+"/css/style.css' rel='stylesheet'>");
    %>
    <style>
        .content{
            width: 100%;
            outline: none;
            font-size: 20px;
            border: 0px;
        }
        #attach-form{
            position: relative;
            width: 100%;
        }
        #attach{
            position: absolute;
            height: 100%;
            width: 10%;
            opacity: 0;
            cursor: pointer;
        }
        .attach-ul{
            position: absolute;
            top: 0px;
            left: 12%;
            width: 88%;
            list-style: none;
            padding: 0px;
            font-size: 14px;
            padding-top: 7px;

        }
        .attach-li{
            float: left;
            margin-right: 2%;
        }
        .attach-li a{
            cursor: pointer;
        }
        .progress{
            display: none;
        }
        .btn-pic{
            width: 100%;
            height: 50%;
            border: 0px;
            border-bottom: 1px solid #ccc;
        }
        #pic{
            position: absolute;
            width: 100%;
            height: 50%;
            opacity: 0;
            top: 0px;
            cursor: pointer;
        }
        #content img{
            vertical-align: bottom;
            max-width: 50%;
        }
        #pic-draggable,#timer-draggable{
            position:absolute;
            top:50%;
            left:30%;
            transform:translateX(-50%) translateY(-50%);
            -moz-transform:translateX(-50%) translateY(-50%);
            -webkit-transform:translateX(-50%) translateY(-50%);
            -ms-transform:translateX(-50%) translateY(-50%);
            font-size:9pt;
            padding:30px;
            width:50%;
            z-index: 2000;
            display: none;
        }
        #pic-draggable{
            width:50%;
        }
        .reply{
            height: 200px;
            width: 300px;
            background: red;
        }
    </style>
</head>
<body>
<div id="loading" style="display:none;position: absolute;top: 0px;z-index:1000;height: 140%;width: 100%;background: grey;opacity: 0.5" >
    <img style='margin:25% auto;display: block;' src="./static/img/loading.gif" />
</div>
<div  id="load-text" style="position:absolute;display: none;top: 50%;left: 48%;font-size: 100%;z-index: 1111;color: #808A87">发送中...</div>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 id="mail-title" class="panel-title">写信</h3>
    </div>
    <div class="panel-body">
        <ul class="list-group">
            <li class="list-group-item" style="padding: 0px;border: 0px">
                <div class="input-group">
                    <div class="input-group-btn">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">发送邮箱 <span class="caret"></span></button>
                        <ul id="bindMail" class="dropdown-menu">
                        </ul>
                    </div>
                    <input type="text" readonly id="from" class="form-control" aria-label="...">
                </div>
            </li>
            <li class="list-group-item" style="padding: 0px;border: 0px">
                <div class="input-group">
                    <span class="input-group-addon" >收件人</span>
                    <input type="text" id="to"  class="form-control" placeholder="Receiver" aria-describedby="sizing-addon2">
                </div>
            </li>
            <li class="list-group-item" style="padding: 0px;border: 0px">
                <div class="input-group">
                    <span class="input-group-addon" >主题</span>
                    <input type="text" id="subject" class="form-control" placeholder="Topic" aria-describedby="sizing-addon2">
                </div>
            </li>
        </ul>
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">正文</h3>
            </div>
            <div class="panel-body" style="padding-bottom: 5%">
                <div contentEditable="true" autofocus id="content" class="content" >
                </div>
            </div>
        </div>
        <form id="attach-form" enctype="multipart/form-data">
            <input id="attach" type="file" name="attach">
            <button type="button" style="cursor: pointer" class="btn btn-default">添加附件</button>
            <ul class="attach-ul">
            </ul>
            <div class="progress">
                <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                </div>
            </div>
        </form>
        <div class="btn-group" role="group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                添加图片
                <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" style="padding-top: 0px;padding-bottom: 0px;top: -200%">
                <li>
                    <form style="margin: 0px" id="pic-form">
                        <input type="file" name="pic" id="pic">
                    </form>

                    <button id="btn-pic" type="button" class="btn btn-default btn-pic">
                        本地图片
                    </button>
                </li>
                <li>
                    <button type="button" id="web-pic-btn" class="btn btn-default btn-pic">
                        网络图片
                    </button>
                </li>
            </ul>
        </div>
        <button type="button" id="saveDraft" class="btn btn-default">存为草稿</button>

        <button type="button" id="sendTimer" class="btn btn-default">定时发送</button>
        <button type="button" id="send" class="btn btn-default">发送</button>
        <div  id="pic-draggable">
            <div class="panel panel-primary web-pic-div">
                <div class="panel-heading">
                    <h3 class="panel-title">网络地址</h3>
                </div>
                <div class="panel-body ">
                    <input type="text" id="web-pic-url" placeholder="url" class="form-control">
                    <div style="margin-top: 1%;">
                        <button id="use-web-pic" style="margin-right: 1%" type="button" class="btn btn-success">使用</button>
                        <button type="button" id="cancel-web-pic" class="btn btn-danger">取消</button>
                    </div>
                </div>
            </div>
        </div>
        <div id="timer-draggable">
            <div class="panel panel-primary" >
                <div class="panel-heading">
                    <h3 class="panel-title">定时发送</h3>
                </div>
                <div class="panel-body ">
                    <input id="date" class="form-control" type="date"/>
                    <input id="time" autofocus step="1" class="form-control" type="time"/>
                    <div style="margin-top: 1%;">
                        <button id="use-timer" style="margin-right: 1%" type="button" class="btn btn-success">确定</button>
                        <button type="button" id="cancel-timer" class="btn btn-danger">取消</button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>
<script>
    var attachment = {};
    var uploading = false;
    $("#attach").on("change", function(){
        if(uploading){
            alert("文件正在上传中，请稍候");
            return false;
        }
        var filename =getFileName($("#attach").val());
        $('.progress-bar').css('width',0+'%');
        $('.progress').css('display','block');   //这里指的是进度条的宽度等于完成的百分比
        if (filename!="") {
            $.ajax({
                url: "./attachment/upload",
                type: 'POST',
                cache: false,
                data: new FormData($('#attach-form')[0]),
                processData: false,
                contentType: false,
                dataType:"json",
                beforeSend: function(){
                    uploading = true;
                },
                xhr: function() {        //ajax进度条
                    var xhr = $.ajaxSettings.xhr();
                    if (xhr.upload) {
                        xhr.upload.addEventListener("progress", function (e) {
                            var loaded = e.loaded; //已经上传大小情况
                            var tot = e.total; //附件总大小
                            console.log("总"+tot)
                            var per = Math.floor(100 * loaded / tot); //已经上传的百分比
                            $('.progress-bar').html(per+'%');
                            $('.progress-bar').css('width',per+'%');   //这里指的是进度条的宽度等于完成的百分比
                            if (per==100){
                                setTimeout(function () {
                                    $('.progress').css('display','none');
                                    showAttach();
                                },1500)

                            }
                        }, false);
                        return xhr;
                    }
                },
                success : function(data) {
                    path = data.data;
                    attachment[path] = filename;
                    uploading = false;
                }
            });
        }
    });
    function showAttach(){
        $('.attach-ul').empty();
        console.log(attachment)
        for (var file in attachment){
            $('.attach-ul').append('  <li class="attach-li"><a href="./attachment/download?type=local&path='+file+'"'+' title="下载">'+attachment[file]+'</a>&nbsp;&nbsp;<a onclick="removeAttach('+"'"+file+"'"+')">删除</a></li>')
        }
    }
    $('#time').hover(function () {
        $('#time').focus();
        console.log($('#time').val()+","+$('#date').val())
    })
    $(document).ready(function () {
        dragFunc('pic-draggable');
        dragFunc('timer-draggable');

        var bind = localStorage.getItem("bind");
        bind = bind.split(",");
        $('#from').val(bind[0]);
        for (var i=0;i<bind.length;i++){
            $('#bindMail').append('<li><a onclick="useMail('+"'"+bind[i]+"'"+')">'+bind[i]+'</a></li>')
        }
    })
    function useMail(e) {
        $('#from').val(e);
    }
    function getMail() {
        var mail = {
            "mid":"",
            "subject":$('#subject').val(),
            "content":null,
        };
        return mail;
    }
    function sendRequest(url,data,callback) {
        $.ajax({
            url: url,
            method:"post",
            data:data,
            beforeSend:function(XMLHttpRequest){
                $("#loading").css({"display":"block"});
                $("#load-text").css({"display":"block"});
            },
            complete: function () {
                $("#loading").css({"display":"none"});
                $("#load-text").css({"display":"none"});
            },
            success : function (res) {
                res = JSON.parse(res)
                if (res.code == 200) {
                    callback(res.msg);
                }
            },
        })
    }
    $('#send').click(function () {
        send();
    })
    $('#sendTimer').click(function () {
        var myDate = new Date();
        $('#time').attr("min",myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds());
        $('#time').val(myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds());
        $('#date').val(myDate.getFullYear()+"/"+myDate.getMonth()+"/"+myDate.getDay())
        $('#timer-draggable').show();
    })
    $('#cancel-timer').click(function () {
        $('#timer-draggable').hide()
    })
    $('#use-timer').click(function () {
        var at = $('#date').val()+" "+$('#time').val();
         send(at);
        $('#timer-draggable').hide()
    })
    function send(at){
        var send = {
            "sid":"",
            "uid":$.cookie("uid"),
            "mid":"",
            "create":"",
            "status":"sending",
            "from": $('#from').val(),
            "to": $('#to').val(),
            "subject":$('#subject').val()
        };
        var mail = getMail();
        var insert = true;
        if ($("#content:has(img)").length==0){
            insert = false;
        }
        var att;
        if (Object.keys(attachment).length>0){
            att = Object.keys(attachment).join(",")
        }else {
            att = null;
        }
        var data = {
            mail: JSON.stringify(mail),
            send: JSON.stringify(send),
            attachment: att,
            insert:insert,
            blob:$('#content').html(),
            at:at
        };
        sendRequest("./mail/send",data,function (msg) {
            alert(msg)
            window.location.reload();
        });
    }
    $('#saveDraft').click(function () {
        var draft = {
            "did":"",
            "uid":$.cookie("uid"),
            "mid":"",
            "create":"",
            "from": $('#from').val(),
            "to":   $('#to').val(),
            "subject":$('#subject').val()
        };
        var mail = getMail();
        var insert = true;
        if ($("#content:has(img)").length==0){
            insert = false;
        }
        var att;
        if (Object.keys(attachment).length>0){
            att = Object.keys(attachment).join(",")
        }else {
            att = null;
        }
        var data = {
            mail: JSON.stringify(mail),
            draft: JSON.stringify(draft),
            attachment: att,
            insert:insert,
            blob:$('#content').html()
        };
        sendRequest("./mail/saveDraft",data,function () {
            alert("保存成功");
        });
    })

    function removeAttach(path,aid) {
        $.post("./attachment/remove",{"path":path,"aid":aid},function (res) {
            res = JSON.parse(res)
            if (res.code==200){
                delete attachment[path];
                showAttach();
            } else{
                alert(res.msg)
            }
        })
    }
    $('#pic').change(function (e) {
        var files = this.files;
        var file;
        if (files && files.length) {
            file = files[0];
            if (/^image\/png$|jpeg$/.test(file.type)) {
                insertImg(file);
            } else {
                alert("Please choose an image file with jpg jpeg png.");
            }
        } else {
            alert("Please choose image file");
        }
    })

    function insertImg(file) {
        $('.progress-bar').css('width',0+'%');
        $('.progress').css('display','block');
        $.ajax({
            url: "./attachment/upload",
            type: 'POST',
            cache: false,
            data: new FormData($('#pic-form')[0]),
            processData: false,
            contentType: false,
            dataType:"json",
            xhr: function() {
                var xhr = $.ajaxSettings.xhr();
                if (xhr.upload) {
                    xhr.upload.addEventListener("progress", function (e) {
                        var loaded = e.loaded; //已经上传大小情况
                        var tot = e.total; //附件总大小
                        var per = Math.floor(100 * loaded / tot); //已经上传的百分比
                        $('.progress-bar').html(per+'%');
                        $('.progress-bar').css('width',per+'%');   //这里指的是进度条的宽度等于完成的百分比
                        if (per==100){
                            setTimeout(function () {
                                $('.progress').css('display','none');
                            },1500)

                        }
                    }, false);
                    return xhr;
                }
            },
            success : function(data) {
                path = data.data;
                addImg(path,getObjectURL(file));
            }
        });
    }
    function addImg(url,path) {
        var editor = document.getElementById('content');
        var img = document.createElement('img');
        img.alt = path;
        img.src = url;
        editor.appendChild(img);
    }

    $("#pic").hover(function (){
        $("#btn-pic").css({"background-color":"rgb(204,204,204)"})
    },function (){
        $("#btn-pic").css({"background-color":"white"})
    });
    $('#web-pic-btn').click(function () {
        $("#pic-draggable").show()
    })
    $('#cancel-web-pic').click(function () {
        $("#pic-draggable").hide()
    })
    $('#use-web-pic').click(function () {
        if ($('#web-pic-url').length>0){
            addImg($('#web-pic-url').val(),"");
            $("#pic-draggable").hide();
        }
    })
</script>