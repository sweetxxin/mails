<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="panel panel-primary panel-div">
    <div class="panel-heading">
        <h3 class="panel-title">收件箱</h3>
    </div>
    <jsp:include page="common/table.jsp"/>
</div>
</body>
</html>
<script>
    var receives =  [];
    $(document).ready(function () {

       var bind = localStorage.getItem("bind");
        bind = bind.split(",");
       if (localStorage.getItem("receives")!=""){
           receives = JSON.parse(localStorage.getItem("receives"));
           show(receives);
       }
        val()
        setTimeout(function () {
            setInterval(function () {
                    val()
            },10000)
        },5000)

    })
    function deleteReceive(mid,uid) {
        $.post("./mail/delete",{"uid":$.cookie("uid"),"mid":mid,"type":"receive"},function (res) {
            alert(res);
            val();
        })
    }
    function filterNewReceive(r) {
        var ret = [];
        var j=0;
        for (var i in r){
            if (!JSON.stringify(receives).includes(JSON.stringify(r[i]))){
                ret[j++]=r[i];
                receives.push(r[i])
            }
        }
        localStorage.setItem("receives",JSON.stringify(receives));
        return ret;
    }
    function val() {
        $.post("./mail/getReceive",{"uid":$.cookie("uid")},function (re) {
            re = JSON.parse(re);
            re.data =filterNewReceive(re.data)
            if (re.data.length>0){
                console.log("排序前"+JSON.stringify(re.data));

                show(re.data);

            }
        })
    }
    function show(data) {
        for (var i in data){
            document.getElementById("tbody").innerHTML =
                '<tr class="tr-class '+data[i].status+'" >' +
                '      <td class="from" onclick=getDetail('+"'"+data[i].mid+"'"+","+"'"+data[i].from+"'"+","+"'"+data[i].to+"'"+')>'+data[i].from+'</td>' +
                '      <td class="to" onclick=getDetail('+"'"+data[i].mid+"'"+","+"'"+data[i].from+"'"+","+"'"+data[i].to+"'"+')>'+data[i].to+'</td>' +
                '      <td class="subject" onclick=getDetail('+"'"+data[i].mid+"'"+","+"'"+data[i].from+"'"+","+"'"+data[i].to+"'"+')>'+data[i].subject+'</td>' +
                '      <td class="create" onclick=getDetail('+"'"+data[i].mid+"'"+","+"'"+data[i].from+"'"+","+"'"+data[i].to+"'"+') class="create">'+format(data[i].create)+'</td>' +
                '      <td>  <button  style="padding: 1px 12px" class="btn btn-primary" onclick="deleteReceive('+"'"+data[i].mid+"'"+')" type="button">删除</button></td>' +
                '      <td>  <button onclick=reply('+"'"+data[i].mid+"'"+","+"'"+data[i].from+"'"+","+"'"+data[i].to+"'" +","+"'"+data[i].create+"'"+') style="padding: 1px 12px" type="button" id="reply"  class="btn btn-success">回复</button></td>'+
                '</tr>'+document.getElementById("tbody").innerHTML;
        }
    }
</script>