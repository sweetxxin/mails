<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        .tr-class:hover{
            background-color: #ccc;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 class="panel-title">已发送</h3>
    </div>
    <jsp:include page="common/table.jsp"/>
</div>
</body>
</html>
<script>
    $(document).ready(function () {
        val()
    })
    function val() {
        $('tr').remove('.tr-class');
        $.post("./mail/already",{"uid":$.cookie("uid")},function (re) {
            re = JSON.parse(re);
            if (re.code==200){
                for (var i in re.data){
                    document.getElementById("tbody").innerHTML =
                        '<tr class="tr-class" onclick=getDetail('+"'"+re.data[i].mid+"'"+","+"'"+re.data[i].from+"'"+","+"'"+re.data[i].to+"'"+')>' +
                        '      <td class="from">'+re.data[i].from+'</td>' +
                        '      <td class="to">'+re.data[i].to+'</td>' +
                        '      <td class="subject">'+re.data[i].subject+'</td>' +
                        '      <td class="create">'+format(re.data[i].create)+'</td>' +
                        '      <td>  <button  style="padding: 1px 12px" class="btn btn-primary" onclick="deleteAlready('+"'"+re.data[i].mid+"'"+')" type="button">删除</button></td>' +
                        '</tr>'+document.getElementById("tbody").innerHTML;
                }
            }
        })
    }
    function deleteAlready(mid) {
        $.post("./mail/delete",{"uid":$.cookie("uid"),"mid":mid,"type":"already"},function (res) {
            alert(res);
            val();
        })
    }
    function add0(m){return m<10?'0'+m:m }
    function format(shijianchuo)
    {
        var time = new Date(shijianchuo);
        var y = time.getFullYear();
        var m = time.getMonth()+1;
        var d = time.getDate();
        var h = time.getHours();
        var mm = time.getMinutes();
        var s = time.getSeconds();
        return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
    }
    //对数组进行排序
    function compare(property) {
        return (firstobj, secondobj) => {
            firstValue = firstobj[property];
            secondValue = secondobj[property];
            return  firstValue-secondValue ; //降序
        };
    }
</script>