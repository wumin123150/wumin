<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
	<title>500错误</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta charset="utf-8" />
	<meta name="description" content="Common form elements and layouts" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />

	<link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
  <style>
  body {
    color: #666;
    text-align: center;
    font-family: Helvetica, 'microsoft yahei', Arial, sans-serif;
    margin:0;
    width: 800px;
    margin: auto;
    font-size: 14px;
  }
  h1 {
    font-size: 56px;
    line-height: 100px;
    font-weight: normal;
    color: #456;
  }
  h2 {
    font-size: 24px;
    color: #666;
    line-height: 1.5em;
  }
  h3 {
    color: #456;
    font-size: 20px;
    font-weight: normal;
    line-height: 28px;
    margin: 15px 0;
  }
  
  hr {
    margin: 18px 0;
    border: 0;
    border-top: 1px solid #EEE;
    border-bottom: 1px solid white;
  }
  
  a {
    color: #17bc9b;
    text-decoration: none;
  }
  </style>
</head>	
<body>
  <h1>500</h1>
  <h3>您要访问的页面无法正常显示.</h3>
  <hr/>
  <p>服务器内部<a id="detail" href="javascript:void(0);">错误</a>，请刷新页面重试<span id="back">，<a href="${ctx}">点击这里</a> 回到首页</span>.</p>
  <div id="exception" style="display:none;word-break:break-all;">
    <c:out value="${exception}" /><br>
    <c:forEach var="trace" items="${exception.stackTrace}">
    <p>${trace}</p>
    </c:forEach>
  </div>
</body>
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script>
  jQuery(function ($) {
    $("#detail").click(function(){
      $('#exception').toggle();
    });
    if (self != top) {
      $('#back').hide();
    }
  })
</script>
</html>
