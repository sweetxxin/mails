<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

</head>
<body>
<jsp:include page="common/head.jsp"></jsp:include>
<jsp:include page="common/nav.jsp"></jsp:include>
<jsp:include page="common/mail.jsp"/>
</body>
</html>
<script src="static/js/common.js"></script>
<script>
 $(document).ready(function () {
     $.post("./mail/seen",{"mid":"${param.mid}"},function (res) {
         console.log(res)
     })
     $.post("./mail/mail",{"mid":"${param.mid}"},function (res) {
         res = JSON.parse(res);
         if (res.code==200){
             $('#from').val("${param.from}");
             $('#to').val("${param.to}");
             $('#subject').val(res.data.mail.subject);
             $('#content img').each(function (index,img) {
                $(img).attr("src","./static"+$(img).attr("alt"));
             });
             if (res.data.attachment!=null){
                for (var i in res.data.attachment) {
                    $('.attach-ul').append('<li class="attach-li"><a href="./attachment/download?path='+res.data.attachment[i].path+'&type=local"'+' title="下载">'+getFileName(res.data.attachment[i].path)+'</a>&nbsp;&nbsp;<a onclick="removeAttach('+"'"+res.data.attachment[i].path+"'"+","+"'"+res.data.attachment[i].aid+"'"+')">删除</a></li>')
                }
             }
             var model = '<div><br></div><div><br></div><div><br></div><div style="line-height: 1.5; color: rgb(0, 0, 0); font-size: 12px; font-family: &quot;Arial Narrow&quot;; padding: 2px 0px;">' +
                 '------------------&nbsp;原始邮件&nbsp;------------------' +
                 '</div>';
             model += '<div style="line-height: 1.5; color: rgb(0, 0, 0); font-family: Verdana; font-size: 12px; background: rgb(239, 239, 239); padding: 8px;">' +
                 '<div style="line-height: 1.5;">' +
                 '<b>发件人:</b>&nbsp;&lt;"${param.from}"&gt;;' +
                 '</div>' +
                 '<div style="line-height: 1.5;">' +
                 '<b>发送时间:</b>&nbsp;' +"${param.time}"+
                 '</div>' +
                 '<div style="line-height: 1.5;">' +
                 '<b>收件人:</b>&nbsp;&lt;'+"${param.from}"+'&gt;;<wbr>' +
                 '</div>' +
                 '<div style="line-height: 1.5;">' +
                 '</div>' +
                 '<div style="line-height: 1.5;"><b>主题:</b>&nbsp;' +res.data.mail.subject+
                 '</div>' + '</div>';
             if ("${param.type}"=="reply"){
                 $('#from').val("${param.to}");
                 $('#to').val("${param.from}");
                 $('#subject').val("回复: "+res.data.mail.subject);
                 $('#content').append(model);
                 $('#content').append(res.data.content)
             }else if("${param.type}"=="forward"){
                 $('#from').val("${param.from}");
                 $('#to').val("");
                 $('#subject').val("转发: "+res.data.mail.subject);
                 $('#content').append(model);
                 $('#content').append(res.data.content)
             }
             else {
                 $('#saveDraft').after('<button onclick=forward('+"'"+"${param.mid}"+"'"+","+"'"+"${param.from}"+"'"+","+"'"+"${param.to}"+"'" +","+"'"+"${param.create}"+"'"+')  type="button" id="reply"  class="btn btn-default">转发</button>');
                 $('#send').replaceWith('<button onclick=reply('+"'"+"${param.mid}"+"'"+","+"'"+"${param.from}"+"'"+","+"'"+"${param.to}"+"'" +","+"'"+"${param.create}"+"'"+')  type="button" id="reply"  class="btn btn-default">回复</button>');
                 $('#reply').after('<button type="button" style="position:absolute;right:10%;" id="back"  class="btn btn-info" onclick=back('+"${param.type}"+')>返回</button>')
                 $('#content').html(res.data.content);
                 $('#mail-title').html("邮件详情");
             }
         }
     })
     $('#content').hover(function () {
         $('#content').focus()
         // set_focus($("#content"));
     })
     function set_focus(el) {
         el = el[0];  // jquery 对象转dom对象
         el.focus();
         if($.support.msie)
         {
             var range = document.selection.createRange();
             this.last = range;
             range.moveToElementText(el);
             range.select();
             document.selection.empty(); //取消选中
         }
         else
         {
             var range = document.createRange();
             range.selectNodeContents(el);
             range.collapse(false);
             var sel = window.getSelection();
             sel.removeAllRanges();
             sel.addRange(range);
         }
     }
 })
 function back(type){
     window.location = "main.jsp?page=receive";
 }

</script>