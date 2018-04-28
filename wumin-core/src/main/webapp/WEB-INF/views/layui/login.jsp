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
  <title>登录</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <style>
    .login {
      height: 260px;
      width: 260px;
      padding: 20px;
      background-color: rgba(0,0,0,0.5);
      border-radius: 4px;
      position: absolute;
      left: 50%;
      top: 50%;
      margin: -150px 0 0 -150px;
      z-index: 99;
    }

    .login h1 {
      text-align: center;
      color: #fff;
      font-size: 24px;
      margin-bottom: 20px;
    }

    .input-captcha {
      position: relative;
    }

    .input-captcha .captcha {
      position: absolute;
      right: 0;
      top: 1px;
      cursor: pointer;
    }

    .login-btn {
      width: 100%;
    }
  </style>
</head>
<body>
<div class="login">
  <h1>登录</h1>
  <form class="layui-form" action="${ctx}/login" method="post">
    <div class="layui-form-item">
      <input class="layui-input" name="username" placeholder="用户名" lay-verify="required" type="text" autocomplete="off">
    </div>
    <div class="layui-form-item">
      <input class="layui-input" name="password" placeholder="密码" lay-verify="required" type="password" autocomplete="off">
    </div>
    <div class="layui-form-item input-captcha">
      <input class="layui-input" name="code" placeholder="验证码" lay-verify="required" type="text" autocomplete="off">
      <div class="captcha"><img src="${ctx}/Kaptcha.jpg" width="116" height="36" style="cursor:pointer;" title="点击刷新"></div>
    </div>
    <button class="layui-btn login-btn" lay-submit lay-filter="login">登录</button>
  </form>
</div>
<script src="${ctx}/static/js/layui/layui.js"></script>
<script>
  if (window.top !== window.self) {
    window.top.location = window.location;
  }

  layui.use(['form','layer'],function(){
    var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery;

    $('.captcha').on('click', function() {
      $('.captcha > img')[0].src = '${ctx}/Kaptcha.jpg?' + Date.now() + Math.random();
    });

    form.on("submit(login)",function(data){
      var loadIndex = layer.load(0, { shade: [0.1,'#000'] });
      $.ajax({
        url: data.form.action,
        type: 'POST',
        cache: false,
        data: data.field,
        dataType: 'json',
        success: function (result) {
          if (result.success) {
            window.sessionStorage.setItem("screenlock",false);
            setTimeout(function() {
              location.href = '${ctx}/admin/index'
            }, 1000);
          } else {
            layer.msg(result.message, {icon: 2});
            loadIndex && layer.close(loadIndex);
            $('.captcha').click(); //刷新验证码
          }
        },
        error: function () {
          loadIndex && layer.close(loadIndex);
          layer.msg('未知错误，请联系管理员', {icon: 5});
        }
      });
      return false;
    })
  });
</script>
</body>
</html>