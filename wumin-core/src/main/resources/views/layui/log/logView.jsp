<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>日志查看</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
  </style>
</head>
<body class="kit-main">
<form class="layui-form" style="width:80%;">
  <div class="layui-form-item">
    <label class="layui-form-label">操作描述</label>
    <div class="layui-form-mid" id="form-description"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">操作用户</label>
    <div class="layui-form-mid" id="form-username"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">操作时间</label>
    <div class="layui-form-mid" id="form-logTime"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">访问路径</label>
    <div class="layui-form-mid" id="form-url"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">访问方式</label>
    <div class="layui-form-mid" id="form-userAgent"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">请求参数</label>
    <div class="layui-form-mid" id="form-parameter"></div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">响应结果</label>
    <div class="layui-form-mid" id="form-result"></div>
  </div>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn layui-btn-primary btn-close">关闭</button>
    </div>
  </div>
</form>
<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  var view_url = "${ctx}/api/log/get/${id}";
  layui.use(['form'], function () {
    var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery;

    $.ajax({
      url: view_url,
      type: 'GET',
      cache: false,
      dataType: 'json',
      success: function (result) {
        if (result.success) {
          $('#form-description').text(result.data.description);
          $('#form-username').text(result.data.username + '(' + result.data.ip + ')');
          $('#form-logTime').text(result.data.logTime + '  耗时' + result.data.spendTime + '秒');
          $('#form-url').text('[' + result.data.method + ']' + result.data.url);
          $('#form-userAgent').text(result.data.userAgent);
          $('#form-parameter').text(result.data.parameter);
          $('#form-result').text(result.data.result);
        } else {
          layer.msg(result.message, {icon: 2});
        }
      },
      error: function () {
        layer.msg('未知错误，请联系管理员', {icon: 5});
      }
    });

    $('.btn-close').on('click', function(){
      parent.layer.closeAll("iframe");
      return false;
    });
  });
</script>
</body>
</html>