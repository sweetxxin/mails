<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 class="panel-title">回收站</h3>
    </div>
    <!-- Table -->
    <table class="table" id="table">
        <th>发送人</th>
        <th>收件人</th>
        <th>主题</th>
        <th>时间</th>
        <th><button  id="clear" style="padding: 1px 12px" class="btn btn-primary"  type="button">清空</button></th>
        <tbody id="tbody"></tbody>
    </table>
</div>
</body>
</html>

<script>
    $(document).ready(function () {
        val()
    })
    function val() {
        $('tr').remove('.tr-class');
        $.post("./mail/recycle",{"uid":$.cookie("uid")},function (re) {
            re = JSON.parse(re);
            if (re.code==200){
                for (var i in re.data){
                    document.getElementById("tbody").innerHTML =
                        '<tr class="tr-class">' +
                        '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="from">'+re.data[i].from+'</td>' +
                        '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="to">'+re.data[i].to+'</td>' +
                        '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="subject">'+re.data[i].subject+'</td>' +
                        '      <td onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+') class="create">'+format(re.data[i].create)+'</td> '+
                        '      <td><button  style="padding: 1px 12px" class="btn btn-primary" onclick="deleteRecycle('+"'"+re.data[i].mid+"'"+')" type="button">删除</button></td>' +
                        '</tr>'+document.getElementById("tbody").innerHTML;
                }
            }
        })
    }
    function deleteRecycle(mid) {
        $.post("./mail/delete",{"mid":mid,"type":"recycle"},function (res) {
            alert(res);
            val();
        })
    }
    $('#clear').click(function () {
        $.post("./mail/clear",{"uid":$.cookie('uid')},function (res) {
            alert(res)
            val();
        })
    })
</script>