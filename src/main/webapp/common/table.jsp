<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        .tr-class:hover{
            background-color: #ccc;
            cursor: pointer;
        }
        thead{
            display: block;
        }
        #table{
            overflow: auto;
            height: 70%;
            display: block;
        }
        .from{
            width:20%;
        }
        .to{
            width: 20%;
        }
        .subject{
            width: 35%;
        }
        .create{
            width: 20%;
        }
        .unSee{
            font-weight: bold;
        }
    </style>
</head>
<body>
    <table class="table" id="table">
        <tbody id="title">
        <th>发送人</th>
        <th>收件人</th>
        <th>主题</th>
        <th>时间</th>
        </tbody>
        <tbody id="tbody">
        </tbody>
    </table>
</body>
</html>
