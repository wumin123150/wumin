<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>机构管理</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/css/select2.min.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    .input-required {
      margin-left: 2px;
      color: #c00;
    }
  </style>
</head>
<body class="kit-main">
<form id="form" class="layui-form layui-form-pane" action="${ctx}/api/userGroup/save" method="post" style="width:80%;">
  <input type="hidden" name="id" id="id" value="${userGroup.id}"/>
  <div class="layui-form-item">
    <label class="layui-form-label">上级机构</label>
    <div class="layui-input-inline" style="margin-right: 0px;">
      <input type="hidden" name="parentId" id="parentId" value="${parent.id}"/>
      <input type="text" name="parentName" id="parentName" value="${parent.name}" placeholder="上级机构" class="layui-input" disabled>
    </div>
    <div class="layui-input-inline" style="width: inherit;">
      <button class="layui-btn <c:if test='${not empty userGroup.id}'>layui-btn-disabled</c:if> btn-search" <c:if test='${not empty userGroup.id}'>disabled</c:if>><i class="layui-icon">&#xe615;</i></button>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">代码<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="code" value="${userGroup.code}" lay-verify="required" placeholder="代码" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">名称<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <input type="text" name="name" value="${userGroup.name}" lay-verify="required" placeholder="名称" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">类型<span class="input-required">*</span></label>
    <div class="layui-input-block">
      <select name="type" data-placeholder="类型">
        <option value="0" <c:if test="${userGroup.type eq 0}">selected</c:if>>公司</option>
        <option value="1" <c:if test="${userGroup.type eq 1}">selected</c:if>>部门</option>
        <option value="2" <c:if test="${userGroup.type eq 2}">selected</c:if>>小组</option>
        <option value="3" <c:if test="${userGroup.type eq 3}">selected</c:if>>其他</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">负责人</label>
    <div class="layui-input-block">
      <input type="text" name="directorId" id="directorId" class="select2"/>
    </div>
  </div>
  <div class="layui-form-item" pane="">
    <label class="layui-form-label">角色</label>
    <div class="layui-input-block">
      <c:forEach items="${roles}" var="role">
        <c:set value="false" var="selectedRole"/>
        <c:forEach items="${userGroup.roleList}" var="ownRole">
          <c:if test="${ownRole.code eq role.code}">
            <c:set var="selectedRole" value="true"/>
          </c:if>
        </c:forEach>
        <input type="checkbox" name="roles" value="${role.id}" lay-skin="primary" title="${role.name}" <c:if test="${selectedRole}"> checked</c:if>>
      </c:forEach>
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label">备注</label>
    <div class="layui-input-block">
      <textarea name="remark" placeholder='备注' class="layui-textarea">${userGroup.remark}</textarea>
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
<script src="${ctx}/static/js/select2.min.js"></script>
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

    $('.btn-close').on('click', function(){
      parent.layer.closeAll("iframe");
      return false;
    });

    $('.btn-search').on('click', function(){
      top.layer.open({
        type: 2,
        area: ['300px', '90%'],
        title:"选择机构",
        content: ["${ctx}/admin/userGroup/select", 'no'],
        btn: ['确定', '关闭'],
        yes: function(index, layero){
          var parentId = $(layero.find("iframe")[0].contentWindow.groupId).val();
          var parentName = $(layero.find("iframe")[0].contentWindow.groupName).val();
          $('#parentId').val(parentId);
          $('#parentName').val(parentName);
          top.layer.close(index);
        },
        cancel: function(index){

        }
      });
      return false;
    });

    $.fn.select2.locales['zh-CN'] = {
      formatNoMatches: function () {
        return "没有找到匹配项";
      },
      formatAjaxError: function (jqXHR, textStatus, errorThrown) {
        return "获取结果失败";
      },
      formatInputTooShort: function (input, min) {
        return "至少输入" + min + "位";
      },
      formatInputTooLong: function (input, max) {
        return "最多输入" + max + "位";
      },
      formatSelectionTooBig: function (limit) {
        return "最多能选择" + limit + "项";
      },
      formatLoadMore: function (pageNumber) {
        return "加载结果中…";
      },
      formatSearching: function () {
        return "搜索中…";
      }
    };

    $.extend($.fn.select2.defaults, $.fn.select2.locales['zh-CN']);

    $('.select2').select2({
      placeholder: "用户",
      minimumInputLength: 1,
      ajax: {
        url: "${ctx}/api/user/search",
        dataType: 'json',
        delay: 250,
        data: function (term, pageNo) {
          return {
            searchKey: $.trim(term),	//联动查询的字符
            pageSize: 15,    	//一次性加载的数据条数
            pageNo: pageNo,  	//页码
            time: new Date()  	//测试
          }
        },
        results: function (result, pageNo) {
          var more = (pageNo * 15) < result.data.totalElements; //用来判断是否还有更多数据可以加载
          return {
            results: result.data.content, more: more
          };
        },
        cache: false
      },
      escapeMarkup: function (markup) {
        return markup;
      }, // let our custom formatter work
      formatResult: function (repo) {
        if (repo.loading) return repo.text;

        return repo.name + "(" + repo.loginName + ")";
      },
      formatSelection: function (repo) {
        return repo.name || repo.text;
      }
    });

    $('#directorId').select2("data", {id: "${userGroup.director.id}", text: "${userGroup.director.name}"});
  });
</script>
</body>
</html>