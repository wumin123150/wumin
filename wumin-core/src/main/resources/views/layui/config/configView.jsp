<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>系统设置管理</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    .layui-form-pane .layui-form-label {
      width: 210px;
    }
    .layui-form-pane .layui-input-block {
      margin-left: 210px;
    }
  </style>
</head>
<body class="kit-main">
<form id="form" class="layui-form layui-form-pane" action="${ctx}/api/config/updateAll" method="post" style="width:80%;">
  <input type="hidden" name="id" id="id" value="${config.id}"/>
  <c:forEach var="config" items="${configs}" varStatus="status">
    <div class="layui-form-item <c:if test='${config.type eq 1}'> layui-form-text</c:if>" <c:if test='${config.type eq 2 || config.type eq 3}'>pane=""</c:if>>
      <label class="layui-form-label">${config.name}（${config.code}）<c:if test="${fn:length(config.remark)>1}"><a class="layui-btn layui-btn-xs btn-help" href="javascript:;" data-content="${config.remark}"><i class="layui-icon">&#xe607;</i></a></c:if></label>
      <div class="layui-input-block">
        <input type="hidden" name="configs[${status.index}].id" value="${config.id}">
        <c:choose>
          <c:when test="${config.type eq 0}">
            <input type="text" name="configs[${status.index}].value" value="${config.value}" autocomplete="off" class="layui-input" <c:if test="${not config.editable}"> readonly</c:if>>
          </c:when>
          <c:when test="${config.type eq 1}">
            <textarea name="configs[${status.index}].value" class="layui-textarea" <c:if test="${not config.editable}"> readonly</c:if>>${config.value}</textarea>
          </c:when>
          <c:when test="${config.type eq 2}">
            <c:if test="${not empty options[status.index]}">
            <c:forEach items="${options[status.index]}" var="option">
              <input type="radio" name="configs[${status.index}].value" value="${option.key}" title="${option.value}" <c:if test="${config.value eq option.key}"> checked</c:if> <c:if test="${not config.editable}"> readonly</c:if>>
            </c:forEach>
            </c:if>
          </c:when>
          <c:when test="${config.type eq 3}">
            <c:set value="${fn:split(config.value, ',')}" var="values"/>
            <c:if test="${not empty options[status.index]}">
              <c:forEach items="${options[status.index]}" var="option">
                <c:set value="false" var="selectedConfig"/>
                <c:forEach items="${values}" var="value">
                  <c:if test="${value eq option.key}">
                    <c:set value="true" var="selectedConfig"/>
                  </c:if>
                </c:forEach>
                <input type="checkbox" name="configs[${status.index}].value" value="${option.key}" lay-skin="primary" title="${option.value}" <c:if test="${selectedConfig}"> checked</c:if> <c:if test="${not config.editable}"> readonly</c:if>>
              </c:forEach>
            </c:if>
          </c:when>
          <c:when test="${config.type eq 4}">
            <select name="configs[${status.index}].value" <c:if test="${not config.editable}"> readonly</c:if>>
              <c:if test="${not empty options[status.index]}">
                <c:forEach items="${options[status.index]}" var="option">
                  <option value="${option.key}" <c:if test="${config.value eq option.key}"> selected</c:if>>${option.value}</option>
                </c:forEach>
              </c:if>
            </select>
          </c:when>
          <c:when test="${config.type eq 5}">
            <c:set value="${fn:split(config.value, ',')}" var="values"/>
            <select name="configs[${status.index}].value" multiple <c:if test="${not config.editable}"> readonly</c:if>>
              <c:if test="${not empty options[status.index]}">
                <c:forEach items="${options[status.index]}" var="option">
                  <c:set value="false" var="selectedConfig"/>
                  <c:forEach items="${values}" var="value">
                    <c:if test="${value eq option.key}">
                      <c:set value="true" var="selectedConfig"/>
                    </c:if>
                  </c:forEach>
                  <option value="${option.key}" <c:if test="${selectedConfig}"> selected</c:if>>${option.value}</option>
                </c:forEach>
              </c:if>
            </select>
          </c:when>
        </c:choose>
      </div>
    </div>
  </c:forEach>
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
        data: $('#form').serialize(),//data.field 不支持checkbox
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

    $('.btn-help').on('click', function(){
      layer.tips($(this).data('content'), this, {
        tips: [1, '#009688'],
        time: 4000
      });
    });

    $('.btn-close').on('click', function(){
      parent.layer.closeAll("iframe");
      return false;
    });
  });
</script>
</body>
</html>