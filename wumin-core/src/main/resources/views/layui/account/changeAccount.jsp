<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>个人资料</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    .center {
      text-align: center;
    }
    .layui-upload {
      width: 147px;
      margin: 0 auto;
      padding-bottom: 5px;
      border: 1px solid #e6e6e6;
      background-color: #fff;
      border-radius: 2px;
      clear: both;
    }
    .layui-upload-img {
      width: 147px;
      height: 147px;
      margin-bottom: 5px;
    }
    @media screen and (max-width: 768px) {
      .layui-upload {
        margin-bottom: 15px;
      }
    }
  </style>
</head>
<body class="kit-main">
<div class="layui-tab layui-tab-brief">
  <ul class="layui-tab-title">
    <li class="layui-this">基本信息</li>
    <li>修改密码</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
      <form id="info" class="layui-form layui-form-pane" action="${ctx}/api/account/change" method="post" style="width:80%;">
        <input type="hidden" name="id" value="${user.id}"/>
        <div class="layui-row">
          <div class="layui-col-xs12 layui-col-sm9">
            <div class="layui-form-item">
              <label class="layui-form-label">账号</label>
              <div class="layui-input-block">
                <input type="text" name="loginName" value="${user.loginName}" lay-verify="required" placeholder="建议用手机/邮箱注册" autocomplete="off" class="layui-input" disabled>
              </div>
            </div>
            <div class="layui-form-item">
              <label class="layui-form-label">姓名<span class="input-required">*</span></label>
              <div class="layui-input-block">
                <input type="text" name="name" value="${user.name}" lay-verify="required" placeholder="姓名" autocomplete="off" class="layui-input">
              </div>
            </div>
            <div class="layui-form-item" pane="">
              <label class="layui-form-label">性别</label>
              <div class="layui-input-block">
                <input type="radio" name="sex" value="0" title="保密" <c:if test="${user.sex eq 0}"> checked</c:if>>
                <input type="radio" name="sex" value="1" title="男" <c:if test="${user.sex eq 1}"> checked</c:if>>
                <input type="radio" name="sex" value="2" title="女" <c:if test="${user.sex eq 2}"> checked</c:if>>
              </div>
            </div>
            <div class="layui-form-item">
              <label class="layui-form-label">出生日期</label>
              <div class="layui-input-block">
                <input type="text" name="birthday" id="birthday" value="<fmt:formatDate value='${user.birthday}'/>" placeholder="出生日期" autocomplete="off" class="layui-input">
              </div>
            </div>
          </div>
          <div class="layui-col-xs12 layui-col-sm3 center">
            <div class="layui-upload">
              <c:if test="${not empty user.avatar}">
                <img id="avatar" class="layui-upload-img" src="${ctx}/avatar/${user.id}" onerror="this.src='${ctx}/static/avatars/avatar6.jpg'"/>
              </c:if>
              <c:if test="${empty user.avatar}">
                <img id="avatar" class="layui-upload-img" src="${ctx}/static/avatars/avatar6.jpg"/>
              </c:if>
              <button type="button" class="layui-btn" id="btn-avatar">上传头像</button>
            </div>
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">手机</label>
          <div class="layui-input-block">
            <input type="tel" name="mobile" value="${user.mobile}" placeholder="手机" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">邮箱</label>
          <div class="layui-input-block">
            <input type="text" name="email" value="${user.email}" placeholder="邮箱" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <div class="layui-input-block">
            <button class="layui-btn" lay-submit lay-filter="save">保存</button>
          </div>
        </div>
      </form>
    </div>
    <div class="layui-tab-item">
      <form id="pwd" class="layui-form layui-form-pane" action="${ctx}/api/account/changePwd" method="post" style="width:80%;">
        <input type="hidden" name="id" value="${user.id}"/>
        <div class="layui-form-item">
          <label class="layui-form-label">原密码<span class="input-required">*</span></label>
          <div class="layui-input-block">
            <input type="password" name="password" lay-verify="required" placeholder="原密码" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">新密码<span class="input-required">*</span></label>
          <div class="layui-input-inline">
            <input type="password" name="newPassword" id="password" lay-verify="pass" placeholder="6~16个字符，区分大小写" autocomplete="off" class="layui-input">
          </div>
          <div class="layui-input-inline">
            <input type="password" name="repassword" lay-verify="repass" placeholder="请再次填写密码" autocomplete="off" class="layui-input">
          </div>
          <div class="layui-input-inline">
            <span class="layui-btn layui-btn-primary pswState"> &nbsp;&nbsp;&nbsp; </span>
          </div>
        </div>
        <div class="layui-form-item">
          <div class="layui-input-block">
            <button class="layui-btn" lay-submit lay-filter="save2">保存</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>
<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  layui.use(['form', 'element', 'laydate', 'upload'], function () {
    var form = layui.form,
      element = layui.element,
      layer = layui.layer,
      $ = layui.jquery,
      laydate = layui.laydate,
      upload = layui.upload;

    laydate.render({
      elem: '#birthday'
    });

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

    var uploadInst = upload.render({
      elem: '#btn-avatar'
      ,url: '${ctx}/api/annex/save'
      ,done: function(response){
        if(response.success){
          var path = "${ctx}" + response.data.requestURI;
          $('#avatar').attr('src', path);
          $.ajax({
            url: '${ctx}/api/account/setAvatar',
            type: 'POST',
            cache: false,
            data: {avatarId: response.data.id},
            dataType: 'json',
            success: function (result) {
              if (result.success) {
                toastr.success('保存成功');
              } else {
                toastr.error(result.message);
              }
            },
            error: function () {
              toastr.error('未知错误，请联系管理员');
            }
          });
        } else {
          layer.msg(message, {icon: 2});
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

    form.verify({
      pass: [/^\S{6,16}$/, '密码长度必须6到16位'],
      repass: function(value){
        if(value != $('#password').val()){
          return '必须与密码保持一致';
        }
      }
    });

    form.on('submit(save2)', function (data) {
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
  });
</script>
</body>
</html>