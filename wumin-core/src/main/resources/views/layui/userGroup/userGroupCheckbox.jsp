<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>机构选择</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/zTree/metroStyle/metroStyle.css">
  <style type="text/css">
    .ztree li span.button.switch.level0 {visibility:hidden; width:1px;}
    .ztree li ul.level0 {padding:0; background:none;}
  </style>
</head>
<body class="kit-main">
<ul id="tree" class="ztree"></ul>
<input type="hidden" id="groupIds">
<input type="hidden" id="groupNames">
<script src="${ctx}/static/js/layui/layui.js"></script>
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script src="${ctx}/static/js/zTree/jquery.ztree.core.min.js"></script>
<script src="${ctx}/static/js/zTree/jquery.ztree.excheck.min.js"></script>
</body>
<script>
  layui.use('table', function () {
    var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery;

    var index = parent.layer.getFrameIndex(window.name);

    $('.btn-close').on('click', function(){
      parent.layer.close(index);
      return false;
    });
  });

  var setting = {
    check: {
      enable: true,
      chkboxType: {"Y": "", "N": ""}
    },
    data: {
      simpleData: {
        enable: true,
        idKey: "id",
        pIdKey: "parentId",
        rootPId: null
      }
    },
    view: {
      selectedMulti: true,
      autoCancelSelected: true
    }, callback: {
      onCheck: function (event, treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj("tree"),
          nodes = zTree.getCheckedNodes(true),
          names = "", ids = "";

        for (var i = 0, l = nodes.length; i < l; i++) {
          names += nodes[i].name + ", ";
          ids += nodes[i].id + ",";
        }

        if (names.length > 0) names = names.substring(0, names.length - 2);
        if (ids.length > 0) ids = ids.substring(0, ids.length - 1);
        $("#groupNames").val(names);
        $("#groupIds").val(ids);
      }
    }
  }

  jQuery(function ($) {
    $.get('${ctx}/api/userGroup/list', function(result) {
      if(result.success) {
        for(var i=0;i<result.data.length;i++) {
          if(result.data[i].parentId == null) {
            result.data[i].parentId = 0;
          }
          result.data[i].open = true;
        }
        result.data[result.data.length] = { id:0, parentId:null, name:"组织机构树", open:true, chkDisabled:true};
        $.fn.zTree.init($('#tree'), setting, result.data);
      }
    });
  });
</script>
</html>