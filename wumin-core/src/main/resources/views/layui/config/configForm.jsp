<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>配置管理</title>
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
<blockquote class="layui-elem-quote">请注意Select多选框暂不支持，请使用Checkbox多选框</blockquote>
<form class="layui-form layui-form-pane" action="${ctx}/api/config/save" method="post" style="width:80%;">
  <input type="hidden" name="id" id="id" value="${config.id}"/>
  <div class="layui-form-item">
    <label class="layui-form-label">名称<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="name" value="${config.name}" lay-verify="required" placeholder="名称" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">键<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="code" value="${config.code}" lay-verify="required" placeholder="键" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">值<span class="input-required">*</span></label>
    <div class="layui-input-inline">
      <input type="text" name="value" value="${config.value}" lay-verify="required" placeholder="值" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-input-inline" style="width: inherit;">
      <input type="checkbox" name="editable" value="true" <c:if test="${config.editable}">checked</c:if> title="可修改">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">类型<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <select name="type" data-placeholder="类型">
        <option value="0" <c:if test="${config.type eq 0}">selected</c:if>>Input输入框</option>
        <option value="1" <c:if test="${config.type eq 1}">selected</c:if>>Textarea文本框</option>
        <option value="2" <c:if test="${config.type eq 2}">selected</c:if>>Radio单选框</option>
        <option value="3" <c:if test="${config.type eq 3}">selected</c:if>>Checkbox多选框</option>
        <option value="4" <c:if test="${config.type eq 4}">selected</c:if>>Select单选框</option>
        <option value="5" <c:if test="${config.type eq 5}">selected</c:if> disabled>Select多选框</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label">参数</label>
    <div class="layui-input-block">
      <textarea name="params" placeholder='类型为选择时使用，json格式（例如：{"1":"男","2":"女"}）' class="layui-textarea">${config.params}</textarea>
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label">备注</label>
    <div class="layui-input-block">
      <textarea name="remark" placeholder='备注' class="layui-textarea">${config.remark}</textarea>
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
  });
</script>
</body>
</html>