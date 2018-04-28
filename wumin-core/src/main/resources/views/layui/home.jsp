<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>首页</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
</head>
<body class="kit-main">
<div class="row">
  <div class="col">
    <blockquote class="layui-elem-quote">系统基本参数</blockquote>
    <table class="layui-table">
      <colgroup>
        <col width="150">
        <col>
      </colgroup>
      <tbody>
      <tr>
        <td>当前版本</td>
        <td>2.3.1-GA</td>
      </tr>
      <tr>
        <td>开发作者</td>
        <td>wumin</td>
      </tr>
      <tr>
        <td>已用内存</td>
        <td><fmt:formatNumber type="number" pattern="0.##">${usedMemory/1024/1024}</fmt:formatNumber>MB</td>
      </tr>
      <tr>
        <td>剩余内存</td>
        <td><fmt:formatNumber type="number" pattern="0.##">${freeMemory/1024/1024}</fmt:formatNumber>MB</td>
      </tr>
      <tr>
        <td>最大内存</td>
        <td><fmt:formatNumber type="number" pattern="0.##">${maxMemory/1024/1024}</fmt:formatNumber>MB</td>
      </tr>
      </tbody>
    </table>
  </div>
</div>

<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  layui.use(['form','element','layer','jquery'],function(){
    var form = layui.form,
      layer = parent.layer === undefined ? layui.layer : parent.layer,
      element = layui.element,
      $ = layui.jquery;

    $(".panel a").on("click",function(){
      window.parent.addTab($(this));
    })
  })
</script>
</html>