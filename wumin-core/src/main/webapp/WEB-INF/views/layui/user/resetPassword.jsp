<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>用户管理</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    .input-required {
      margin-left: 2px;
      color: #c00;
    }
  </style>
</head>
<body class="kit-main">
<form class="layui-form layui-form-pane" action="${ctx}/api/user/reset" method="post" style="width:80%;">
  <input type="hidden" name="userId" id="id" value="${user.id}"/>
  <div class="layui-form-item">
    <label class="layui-form-label">账号<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="loginName" value="${user.loginName}" placeholder="建议用手机/邮箱注册" autocomplete="off" class="layui-input" disabled>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">姓名<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="name" value="${user.name}" placeholder="姓名" autocomplete="off" class="layui-input" disabled>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">密码<span class="input-required">*</span></label>
    <div class="layui-input-inline">
      <input type="password" name="password" id="password" lay-verify="pass" placeholder="6~16个字符，区分大小写" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-input-inline">
      <input type="password" name="repassword" lay-verify="repass" placeholder="请再次填写密码" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-inline" style="width: inherit;">
      <span class="layui-btn layui-btn-primary pswState"> &nbsp;&nbsp;&nbsp; </span>
    </div>
  </div>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn" lay-submit lay-filter="save">保存</button>
      <button class="layui-btn layui-btn-primary btn-close">关闭</button>
    </div>
  </div>
</form>
<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  layui.use(['form'], function () {
    var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery;

    $('#password').bind('keyup', function (event) {
      var psw = $(this).val();
      var poor = /(^[0-9]+$)|(^[A-Z]+$)|(^[a-z]+$)|(^[`~!@#$%^&*()+=|\\\][\]\{\}:;'\,.<>/?]+$)/;
      var normal = /(^[0-9A-Z]+$)|(^[0-9a-z]+$)|(^[0-9`~!@#$%^&*()+=|\\\][\]\{\}:;'\,.<>/?]+$)|(^[A-Za-z]+$)|(^[A-Z`~!@#$%^&*()+=|\\\][\]\{\}:;'\,.<>/?]+$)|(^[a-z`~!@#$%^&*()+=|\\\][\]\{\}:;'\,.<>/?]+$)/;
      ;
      if (psw == '') {
        $('.pswState').removeClass("layui-btn-danger").removeClass("layui-btn-warm").addClass("layui-btn-primary").html("&nbsp;&nbsp;&nbsp;");
      } else if (poor.test(psw)) {
        $('.pswState').removeClass("layui-btn-warm").removeClass("layui-btn-primary").addClass("layui-btn-danger").html("弱");
      } else if (normal.test(psw)) {
        $('.pswState').removeClass("layui-btn-danger").removeClass("layui-btn-primary").addClass("layui-btn-warm").html("中");
      } else {
        $('.pswState').removeClass("layui-btn-danger").removeClass("layui-btn-warm").html("强");
      }
    });

    form.verify({
      pass: [/^\S{6,16}$/, '密码长度必须6到16位'],
      repass: function(value){
        if(value != $('#password').val()){
          return '必须与密码保持一致';
        }
      }
    });

    //监听提交
    form.on('submit(save)', function (data) {
      var loadIndex = layer.load();
      $.ajax({
        url: data.form.action,
        type: 'POST',
        cache: false,
        data: data.field,
        dataType: 'json',
        success: function (result) {
          if (result.success) {
            loadIndex && layer.close(loadIndex);
            layer.msg('保存成功', {icon: 1});
            parent.layer.closeAll("iframe");
          } else {
            loadIndex && layer.close(loadIndex);
            var message = '';
            var messages = result.message.split(';');
            for(var i=0;i<messages.length;i++) {
              message += messages[i] + '<br>';
            }
            layer.msg(message, {icon: 2});
            console.log(result.debug);
          }
        },
        error: function () {
          loadIndex && layer.close(loadIndex);
          layer.msg('未知错误，请联系管理员', {icon: 5});
        }
      });
      return false;
    });

    $('.btn-close').on('click', function(){
      parent.layer.closeAll("iframe");
      return false;
    });
  });
</script>
</body>
</html>