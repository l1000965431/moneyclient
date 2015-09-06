<%--
  Created by IntelliJ IDEA.
  User: liumin
  Date: 15/8/26
  Time: 下午2:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.money.Service.user.UserService" %>
<%@ page import="com.money.Service.ServiceFactory" %>
<%!
    private String openId;
    String userId;
    String userPassword;
%>

<%
    String TempOpenId = request.getParameter("openId");
    if (openId == null || (TempOpenId != null && !TempOpenId.equals( openId )) ) {
        openId = request.getParameter("openId");
    }
%>

<script language="javascript">
    function on_binding() {
        if (document.form1.username.value == "") {
            alert("用户名不能为空，请输入用户名！");
            document.form1.username.focus();
            return "";
        }

        if (document.form1.userpassword.value == "") {
            alert("密码不能为空！请输入密码！");
            document.form1.userpassword.focus();
            return "";
        }

        <%
          userId = request.getParameter("username");
          userPassword = request.getParameter("userpassword");

        UserService userService = ServiceFactory.getService("UserService");
        if( userService != null ){
        if(userService.BinddingUserId( openId,userId,userPassword )){
        openId = null;
        }

        }
        %>
        return "绑定成功";
    }
</script>

<html>
<head>
    <title>绑定帐号</title>
</head>
<body>
<form name="form1"  action=""  method="post">
    用户名: <input type=text name=username id=username/><br>
    密码: <input type=password name=userpassword id=userpassword/><br>
    <input type="submit" name="submit" value="绑定" onclick="on_binding();">

</form>
</body>
</html>
