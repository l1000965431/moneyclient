<%@ page import="com.money.Service.GlobalConifg.GlobalConfigService" %>
<%@ page import="com.money.Service.ServiceFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: liumin
  Date: 15/10/21
  Time: 下午4:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>系统初始化脚本</title>
</head>
<body>
<script type="text/javascript">
    <%
    String str = "";
    GlobalConfigService globalConfigService = ServiceFactory.getService("GlobalConfigService");
    if(globalConfigService.initConfigVaule() == 1){
    str = "系统初始化成功!";
    }else{
    str = "系统初始化失败,请联系工作人员调试!";
    }

    %>

</script>
</body>
<%=str%>
</html>
