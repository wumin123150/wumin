<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>附件管理</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <style type="text/css">
    @media screen and (max-width: 768px) {
      #timeRange {
        display: none;
      }
    }
  </style>
</head>
<body>
<div class="kit-table">
  <form class="layui-form" lay-filter="kit-search-form">
    <div class="kit-table-header">
      <div class="kit-search-btns">
        <a href="javascript:;" data-action="batchDel" class="layui-btn layui-btn-sm layui-btn-danger"><i class="layui-icon">&#xe640;</i>删除所选</a>
      </div>
      <div class="kit-search-inputs">
        <div class="kit-search-keyword">
          <input type="text" class="layui-input" name="timeRange" id="timeRange" placeholder="时间范围">
          <input type="hidden" name="startTime" id="startTime"/>
          <input type="hidden" name="endTime" id="endTime"/>
          <input type="text" class="layui-input" name="searchKey" placeholder="搜索关键字.." />
          <button lay-submit lay-filter="search"><i class="layui-icon">&#xe615;</i></button>
        </div>
      </div>
    </div>
  </form>
  <div class="kit-table-body">
    <table id="kit-table" lay-filter="kit-table"></table>
    <script type="text/html" id="kit-table-bar">
      <a class="layui-btn layui-btn-xs" lay-event="download">下载</a>
      <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>
  </div>
</div>
<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  var tableId = 'kit-table';
  var tableFilter = 'kit-table';
  var table_page_url = "${ctx}/api/annex/page2";
  var table_del_url = "${ctx}/api/annex/delete/";
  var table_download_url = "${ctx}/admin/annex/download/";
  var table_batch_del_url = "${ctx}/api/annex/batch/delete";
  layui.use(['table', 'laydate'], function () {
    var table = layui.table,
      layer = layui.layer,
      $ = layui.jquery,
      form = layui.form,
      laydate = layui.laydate;

    laydate.render({
      elem: '#timeRange'
      ,type: 'datetime'
      ,range: '到'
      ,format: 'yyyy-MM-dd HH:mm'
      ,done: function(value, date){
        var idx = value.indexOf(' 到 ');
        if(idx > 0) {
          $('#startTime').val(value.substring(0,idx));
          $('#endTime').val(value.substring(idx+3));
        } else {
          $('#startTime').val('');
          $('#endTime').val('');
        }
      }
    });

    var kitTable = table.render({
      elem: '#' + tableId,
      id: tableId,
      url: table_page_url,
      height: 'full-50',
      cols: [
        [
          { checkbox: true, fixed: true },
          { field: 'name', title: '文件名称', width: 150, sort: true, templet: '<div>{{d.name}}{{#  if(d.type == "jpg" || d.type == "peg" || d.type == "png" || d.type == "gif"){ }} <i class="layui-icon">&#xe64a;</i> {{#  } }}</div>' },
          { field: 'path', title: '存储路径', width: 300 },
          { field: 'objectType', title: '对象类型', width: 90 },
          { field: 'objectId', title: '对象ID', width: 300 },
          { field: 'createTime', title: '创建日期', width: 160, sort: true },
          { fixed: 'right', title: '操作', width: 120, align: 'center', toolbar: '#kit-table-bar' }
        ]
      ],
      even: true,
      page: true,
      limits: [10, 20, 50, 100],
      limit: 10,
      request: {
        pageName: 'pageNo',
        limitName: 'pageSize'
      },
      response: {
        statusName: 'code',
        statusCode: 0,
        msgName: 'message',
        countName: 'total',
        dataName: 'data'
      },
      initSort: {
        field: 'createTime',
        type: 'desc'
      }
    });
    //渲染表单
    form.render(null, 'kit-search-form');
    //监听搜索表单提交
    form.on('submit(search)', function (data) {
      kitTable.reload({
        page: {
          curr: 1 //重新从第1页开始
        },
        where: data.field
      });
      return false;
    });
    //监听排序
    table.on('sort(' + tableFilter + ')', function (obj) {
      kitTable.reload({
        initSort: obj,
        where: {
          sortBy: obj.field,
          sortDir: obj.type
        }
      });
    });
    //监听工具条
    table.on('tool(' + tableFilter + ')', function (obj) {
      var data = obj.data;
      var layEvent = obj.event;

      if (layEvent === 'download') { //下载
        window.location.href = table_download_url + data.id;
      } else if (layEvent === 'del') { //删除
        layer.confirm('你确定要删除吗？', function (index) {
          layer.close(index);
          $.ajax({
            url: table_del_url + data.id,
            type: 'GET',
            cache: false,
            dataType: 'json',
            success: function (result) {
              if (result.success) {
                layer.msg('删除成功', {icon: 1});
                kitTable.reload();
              } else {
                layer.msg(result.message, {icon: 2});
              }
            },
            error: function () {
              layer.msg('未知错误，请联系管理员', {icon: 5});
            }
          });
        });
      } else if (layEvent === 'edit') { //编辑
        //do something
      }
    });
    $('#kit-search-more').on('click', function () {
      $('.kit-search-mored').toggle();
    });
    $('.kit-search-btns > a').off('click').on('click', function () {
      var $that = $(this),
        action = $that.data('action');
      switch (action) {
        case 'batchDel':
          var checkStatus = table.checkStatus(tableId)
            ,data = checkStatus.data;
          var ids = '';
          for(var i=0;i<data.length;i++) {
            if(i == 0) {
              ids = data[i].id;
            } else {
              ids += ',' + data[i].id;
            }
          }

          if(ids.length > 0) {
            $.ajax({
              url: table_batch_del_url,
              data: {ids: ids},
              type: 'POST',
              cache: false,
              dataType: 'json',
              success: function (result) {
                if (result.success) {
                  layer.msg('删除成功', {icon: 1});
                  kitTable.reload();
                } else {
                  layer.msg(result.message, {icon: 2});
                }
              },
              error: function () {
                layer.msg('未知错误，请联系管理员', {icon: 5});
              }
            });
          }
        break;
      }
    });
  });
</script>
</html>