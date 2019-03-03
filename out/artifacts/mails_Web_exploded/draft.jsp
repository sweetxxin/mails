<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 class="panel-title">草稿箱</h3>
    </div>
    <jsp:include page="common/table.jsp"/>
</div>
</body>
</html>
<script>
    $(document).ready(function () {
        val()
    })
    function deleteDraft(mid,uid) {
        $.post("./mail/delete",{"uid":$.cookie("uid"),"mid":mid,"type":"draft"},function (res) {
            alert(res);
            val();
        })
    }
    function val() {
        $('tr').remove('.tr-class');
        $.post("./mail/getDraft",{"uid":$.cookie("uid")},function (re) {
            re = JSON.parse(re);
            if (re.code==200){
                for (var i in re.data){
                    document.getElementById("tbody").innerHTML=
                        '<tr class="tr-class" >' +
                            '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="from">'+re.data[i].from+'</td>' +
                            '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="to">'+re.data[i].to+'</td>' +
                            '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="subject">'+re.data[i].subject+'</td>' +
                            '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="create">'+format(re.data[i].create)+'</td>' +
                            '      <td>  <button  style="padding: 1px 12px" class="btn btn-primary" onclick="deleteDraft('+"'"+re.data[i].mid+"'"+')" type="button">删除</button></td>' +
                            '</tr>'+document.getElementById("tbody").innerHTML
                }
            }
        })
    }
</script>