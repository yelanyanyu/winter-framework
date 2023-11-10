<%--
  Created by IntelliJ IDEA.
  User: lenovo
  Date: 2023/11/9
  Time: 22:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>success</h1>
${requestScope.msg}<br>
${requestScope.error}<br>
name: ${requestScope.bean.name}<br>
gender: ${requestScope.bean.gender}<br>
age: ${requestScope.bean.age}<br>

</body>
</html>
