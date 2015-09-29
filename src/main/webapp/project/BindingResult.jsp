<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!--
Author: W3layouts
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE html>
<html>
<head>
<title>绑定结果</title>
<link href="res/style.css" rel='stylesheet' type='text/css' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="Nature Sign In Form,Login Forms,Sign up Forms,Registration Forms,News latter Forms,Elements"./>
<script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } </script>
<!--webfonts-->
<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
<!--//webfonts-->
</head>
<body>
	<h1></h1>
		<div class="app-nature">
			<div class="nature"><img src="res/timer.png" class="img-responsive" alt="" /></div>
			<form>
            <h1>
				<script type="text/javascript">
					function GetQueryString(name)
					{
						var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
						var r = window.location.search.substr(1).match(reg);
						if(r!=null)return  unescape(r[2]); return null;
					}
					var result = GetQueryString('result');
					if( result != null && result == '1' ){
						document.write('绑定成功!!!!!!!! (^o^)')
					}else if( result != null && result == '2' ){
						document.write('绑定失败!!!!!!!! (＞﹏＜)')
					}else if( result != null && result == '3' ){
						document.write('已绑定!!!!!!!! O(∩_∩)O')
					}
				</script>
			</h1>
			</form>
		</div>
	<!--start-copyright-->
   		<div class="copy-right">
				<p>Copyright &copy; 2015  All rights  Reserved |  &nbsp;<a href="http://www.longan.com">Longan</a></p>
		</div>
	<!--//end-copyright-->
</body>
</html>