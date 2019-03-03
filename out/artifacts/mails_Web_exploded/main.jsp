<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>邮箱转收发系统</title>
    <link type="text/css" rel="stylesheet" href="static/css/style.css">
</head>
<body>
<jsp:include page="common/head.jsp" />
<jsp:include page="common/nav.jsp" />
<%--<jsp:include page="my.jsp"></jsp:include>--%>
<jsp:include page="${param.page==null? 'my':param.page}.jsp" />
</body>
</html>
<script src="./static/js/common.js"></script>
<script>
    var page = "${param.page==null? 'my':param.page}";
    console.log(page)
   $('#'+page).addClass("active")
</script>