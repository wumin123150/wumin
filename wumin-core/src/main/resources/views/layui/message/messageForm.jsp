<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>消息管理</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    ul.ztree {
      margin-top: 0;
      border: 1px solid #d5d5d5;
      background: #fff;
      width: 100% !important;
      height: 200px;
      overflow-y: scroll;
      overflow-x: auto;
    }
  </style>
</head>
<body class="kit-main">
<form class="layui-form layui-form-pane" action="${ctx}/api/messageText/create" method="post" style="width:80%;">
  <input type="hidden" name="id" id="id" value="${role.id}"/>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label">内容<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <textarea name="content" placeholder='内容' class="layui-textarea">${message.content}</textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">接收人<span class="input-required">*</span></label>
    <div class="layui-input-inline" style="margin-right: 0px;width: calc(100% - 165px);">
      <input type="text" name="users" id="users" class="hide"/>
      <input type="text" name="userNames" id="userNames" placeholder="接收人" class="layui-input" disabled/>
    </div>
    <div class="layui-input-inline" style="width: inherit;margin-right: 0px;">
      <button class="layui-btn btn-search"><i class="layui-icon">&#xe615;</i></button>
    </div>
  </div>
  <div class="layui-form-item">
    <div class="layui-input-block kit-btns">
      <button class="layui-btn" lay-submit lay-filter="save">保存</button>
      <button class="layui-btn layui-btn-primary btn-close">关闭</button>
    </div>
  </div>
</form>
<script src="${ctx}/static/js/layui/layui.js"></script>
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
</body>
<script>
  layui.use(['form'], function () {
    var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery;

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

    $('.btn-search').on('click', function(){
      top.layer.open({
        type: 2,
        area: ['400px', '90%'],
        title:"选择接收人",
        content: ["${ctx}/admin/user/treeCheckbox", 'no'],
        btn: ['确定', '关闭'],
        yes: function(index, layero){
          var userIds = $(layero.find("iframe")[0].contentWindow.userIds).val();
          var userNames = $(layero.find("iframe")[0].contentWindow.userNames).val();
          $('#users').val(userIds);
          $('#userNames').val(userNames);
          top.layer.close(index);
        },
        cancel: function(index){

        }
      });
      return false;
    });
  });
</script>
</body>
</html>