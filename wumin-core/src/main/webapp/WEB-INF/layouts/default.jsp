<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <title>${iApplication}</title>

  <!--
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
  <meta http-equiv="Cache-Control" content="no-store" />
  <meta http-equiv="Pragma" content="no-cache" />
  <meta http-equiv="Expires" content="0" />
   -->
  <meta charset="utf-8"/>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
  <meta http-equiv="Access-Control-Allow-Origin" content="*">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

  <!--
  <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
  -->

  <!-- bootstrap & fontawesome -->
  <link rel="stylesheet" href="${ctx}/webjars/bootstrap/css/bootstrap.min.css"/>
  <link rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>

  <sitemesh:write property='pluginCss'/>

  <sitemesh:write property='pageCss'/>

  <!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->

  <!--[if lte IE 8]>
  <script src="${ctx}/static/js/html5shiv.min.js"></script>
  <script src="${ctx}/static/js/respond.min.js"></script>
  <![endif]-->
</head>

<body>
<%@ include file="/WEB-INF/layouts/header.jsp" %>

<div class="main-container" id="main-container">
  <sitemesh:write property='body'/>

  <%@ include file="/WEB-INF/layouts/footer.jsp" %>
</div>

<!-- basic scripts -->
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script src="${ctx}/webjars/bootstrap/js/bootstrap.min.js"></script>

<sitemesh:write property='pluginJs'/>

<sitemesh:write property='pageJs'/>
</body>
</html>