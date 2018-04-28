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
  <link rel="stylesheet" href="${ctx}/static/js/layui/page.css" media="all"/>
  <link rel="stylesheet" href="${ctx}/static/js/zTree/metroStyle/metroStyle.css">
  <style type="text/css">
    .ztree {
      margin-left: 5px;
    }
    .ztree li span.button.switch.level0 {visibility:hidden; width:1px;}
    .ztree li ul.level0 {padding:0; background:none;}
  </style>
</head>
<body>
<div class="layui-row">
  <div class="layui-col-xs3">
    <ul id="tree" class="ztree"></ul>
  </div>
  <div class="layui-col-xs9">
    <div class="kit-table">
      <form class="layui-form" lay-filter="kit-search-form">
        <div class="kit-table-header">
          <div class="kit-search-btns">
            <a href="javascript:;" data-action="add" class="layui-btn layui-btn-sm"><i class="layui-icon">&#xe608;</i>新增</a>
          </div>
          <div class="kit-search-inputs">
            <div class="kit-search-keyword">
              <input type="hidden" id="parentId" name="parentId"/>
              <input type="text" class="layui-input" name="searchKey" placeholder="搜索关键字.." />
              <button id="btn-search" lay-submit lay-filter="search"><i class="layui-icon">&#xe615;</i></button>
            </div>
          </div>
        </div>
      </form>
      <div class="kit-table-body">
        <table id="kit-table" lay-filter="kit-table"></table>
        <script type="text/html" id="kit-table-bar">
          <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="auth">分配用户</a>
          <a class="layui-btn layui-btn-warm layui-btn-xs" lay-event="add">新增下级</a>
          <a class="layui-btn layui-btn-xs" lay-event="edit">修改</a>
          <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
        </script>
        <script type="text/html" id="typeTpl">
          {{#  if(d.type == 0){ }}
          <span class="layui-badge layui-bg-green">公司</span>
          {{#  } else if(d.type == 1) { }}
          <span class="layui-badge layui-bg-green">部门</span>
          {{#  } else if(d.type == 2) { }}
          <span class="layui-badge layui-bg-green">小组</span>
          {{#  } else if(d.type == 3) { }}
          <span class="layui-badge layui-bg-gray">其他</span>
          {{#  } }}
        </script>
        <script type="text/html" id="directorTpl">
          {{#  if(d.director != null){ }}
          {{d.director.name}}
          {{#  } else { }}
          {{#  } }}
        </script>
      </div>
    </div>
  </div>
</div>

<script src="${ctx}/static/js/layui/layui.js"></script>
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script src="${ctx}/static/js/zTree/jquery.ztree.core.min.js"></script>
</body>
<script>
  var tableId = 'kit-table';
  var tableFilter = 'kit-table';
  var table_page_url = "${ctx}/api/userGroup/page2";
  var table_add_url = "${ctx}/admin/userGroup/create?parentId=";
  var table_edit_url = "${ctx}/admin/userGroup/update/";
  var table_del_url = "${ctx}/api/userGroup/delete/";
  var table_get_url = "${ctx}/api/userGroup/get/";
  var table_auth_url = "${ctx}/admin/userGroup/auth/";
  layui.use('table', function () {
    var table = layui.table,
      layer = layui.layer,
      $ = layui.jquery,
      form = layui.form;

    var kitTable = table.render({
      elem: '#' + tableId,
      id: tableId,
      url: table_page_url,
      height: 'full-50',
      cols: [
        [
          { checkbox: true, fixed: true },
          { field: 'id', title: 'ID', width: 80 },
          { field: 'code', title: '代码', width: 100, sort: true },
          { field: 'name', title: '名称', width: 200 },
          { field: 'type', title: '类型', width: 80, templet: '#typeTpl' },
          { field: 'director', title: '负责人', width: 100, templet: '#directorTpl' },
          { fixed: 'right', title: '操作', width: 260, align: 'center', toolbar: '#kit-table-bar' }
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

      if (layEvent === 'view') { //查看
        //do somehing
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
                reloadTree();
              } else {
                layer.msg(result.message, {icon: 5});
              }
            },
            error: function () {
              layer.msg('未知错误，请联系管理员', {icon: 5});
            }
          });
        });
      } else if (layEvent === 'edit') { //编辑
        var index = layer.open({
          title: "修改机构",
          type: 2,
          content: table_edit_url + data.id,
          success: function(layero, index){
            setTimeout(function(){
              layui.layer.tips('点击此处返回机构列表', '.layui-layer-setwin .layui-layer-close', {
                tips: 3
              });
            },1000)
          },
          end: function() {
            kitTable.reload();
            reloadTree();
          }
        })
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
          layer.full(index);
        })
        layer.full(index);
      } else if (layEvent === 'auth') { //分配
        var index = layer.open({
          title: "分配用户",
          type: 2,
          content: table_auth_url + data.id,
          success: function(layero, index){
            setTimeout(function(){
              layui.layer.tips('点击此处返回机构列表', '.layui-layer-setwin .layui-layer-close', {
                tips: 3
              });
            },1000)
          }
        })
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
          layer.full(index);
        })
        layer.full(index);
      } else if (layEvent === 'add') { //新增下级
        var index = layer.open({
          title: "新增机构",
          type: 2,
          content: table_add_url + data.id,
          success: function(layero, index){
            setTimeout(function(){
              layui.layer.tips('点击此处返回机构列表', '.layui-layer-setwin .layui-layer-close', {
                tips: 3
              });
            },1000)
          },
          end: function() {
            kitTable.reload();
            reloadTree();
          }
        })
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
          layer.full(index);
        })
        layer.full(index);
      }
    });
    $('#kit-search-more').on('click', function () {
      $('.kit-search-mored').toggle();
    });

    var tab = parent.tab;
    $('.kit-search-btns > a').off('click').on('click', function () {
      var $that = $(this),
        action = $that.data('action');
      switch (action) {
        case 'add':
          var index = layer.open({
            title: "新增机构",
            type: 2,
            content: table_add_url,
            success: function(layero, index){
              setTimeout(function(){
                layui.layer.tips('点击此处返回机构列表', '.layui-layer-setwin .layui-layer-close', {
                  tips: 3
                });
              },1000)
            },
            end: function() {
              kitTable.reload();
              reloadTree();
            }
          })
          //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
          $(window).resize(function(){
            layer.full(index);
          })
          layer.full(index);
          break;
        case 'batchDel':
          break;
      }
    });
  });

  var reloadTree = function () {
    var treeObj = $.fn.zTree.getZTreeObj("tree");
    var nodes = treeObj.getSelectedNodes();
    var treeNode = nodes[0];
    if (!treeNode) {
      treeObj.reAsyncChildNodes(null, "refresh");
    } else {
      treeObj.reAsyncChildNodes(treeNode, "refresh");
    }
  }

  var setting = {
    async: {
      enable: true,
      url: '${ctx}/api/userGroup/tree',
      dataType: "json",
      autoParam: ["id"],
      dataFilter: function(treeId, parentNode, result) {
        if(result.success) {
          if (parentNode == null) {
            for(var i=0;i<result.data.length;i++) {
              if(result.data[i].parentId == null) {
                result.data[i].parentId = 0;
              }
            }
            result.data[result.data.length] = { id:0, parentId:null, name:"组织机构树", open:true};
          }
          return result.data;
        }
        return [];
      }
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
      selectedMulti: false,
      autoCancelSelected: true
    }, callback: {
      onClick: function(event, treeId, treeNode, clickFlag) {
        if (clickFlag === 0 || treeNode.id == 0) {
          $("#parentId").val('');
        } else {
          $("#parentId").val(treeNode.id);
        }
        $("#btn-search").trigger("click");
      },
      onAsyncSuccess: function (event, treeId, treeNode, msg) {
        if (treeNode == null) {
          var zTree = $.fn.zTree.getZTreeObj(treeId);
          zTree.expandAll(true);
        }
      }
    }
  };

  jQuery(function ($) {
    $.fn.zTree.init($('#tree'), setting);
  });
</script>
</html>