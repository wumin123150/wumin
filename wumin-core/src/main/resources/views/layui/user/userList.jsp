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
  </style>
</head>
<body>
<div class="kit-table">
  <form class="layui-form" lay-filter="kit-search-form">
    <div class="kit-table-header">
      <div class="kit-search-btns">
        <a href="javascript:;" data-action="add" class="layui-btn layui-btn-sm"><i class="layui-icon">&#xe608;</i>新增</a>
      </div>
      <div class="kit-search-inputs">
        <div class="kit-search-keyword">
          <input type="text" class="layui-input" name="searchKey" placeholder="搜索关键字.." />
          <button lay-submit lay-filter="search"><i class="layui-icon">&#xe615;</i></button>
        </div>
        <div class="kit-search-more" id="kit-search-more">更多筛选<i class="layui-icon">&#xe61a;</i></div>
      </div>
    </div>
    <div class="kit-search-mored layui-anim layui-anim-fadein">
      <div class="kit-search-body" style="width:80%;">
        <div class="layui-form-item">
          <label class="layui-form-label">账号</label>
          <div class="layui-input-block">
            <input type="text" name="loginName" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">姓名</label>
          <div class="layui-input-block">
            <input type="text" name="name" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">手机</label>
          <div class="layui-input-block">
            <input type="tel" name="mobile" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">邮箱</label>
          <div class="layui-input-block">
            <input type="text" name="email" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">状态</label>
          <div class="layui-input-block">
            <select name="status">
              <option value=""></option>
              <option value="A">正常</option>
              <option value="E">过期</option>
              <option value="L">锁定</option>
              <option value="T">停用</option>
              <option value="I">未激活</option>
            </select>
          </div>
        </div>
      </div>
      <div class="kit-search-footer">
        <div class="layui-form-item">
          <button type="reset" class="layui-btn layui-btn-sm layui-btn-primary kit-btn">重置</button>
          <button lay-submit lay-filter="search" class="layui-btn layui-btn-sm kit-btn">确定</button>
        </div>
      </div>
    </div>
  </form>
  <div class="kit-table-body">
    <table id="kit-table" lay-filter="kit-table"></table>
    <script type="text/html" id="kit-table-bar">
      <a class="layui-btn layui-btn-warm layui-btn-xs" lay-event="reset">修改密码</a>
      <a class="layui-btn layui-btn-xs" lay-event="edit">修改</a>
      <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">停用</a>
    </script>
    <script type="text/html" id="sexTpl">
      {{#  if(d.sex == 0){ }}
      <span class="layui-badge layui-bg-gray">保密</span>
      {{#  } else if(d.sex == 1) { }}
      <span class="layui-badge layui-bg-green">男</span>
      {{#  } else if(d.sex == 2) { }}
      <span class="layui-badge layui-bg-green">女</span>
      {{#  } }}
    </script>
    <script type="text/html" id="statusTpl">
      {{#  if(d.status == 'A'){ }}
      <span class="layui-badge layui-bg-green">正常</span>
      {{#  } else if(d.status == 'E') { }}
      <span class="layui-badge layui-bg-orange">过期</span>
      {{#  } else if(d.status == 'L') { }}
      <span class="layui-badge layui-bg-cyan">锁定</span>
      {{#  } else if(d.status == 'T') { }}
      <span class="layui-badge">停用</span>
      {{#  } else if(d.status == 'I') { }}
      <span class="layui-badge layui-bg-gray">未激活</span>
      {{#  } else { }}
      <span class="layui-badge layui-bg-gray">未知</span>
      {{#  } }}
    </script>
  </div>
</div>
<script src="${ctx}/static/js/layui/layui.js"></script>
</body>
<script>
  var tableId = 'kit-table';
  var tableFilter = 'kit-table';
  var table_page_url = "${ctx}/api/user/page2";
  var table_add_url = "${ctx}/admin/user/create";
  var table_edit_url = "${ctx}/admin/user/update/";
  var table_del_url = "${ctx}/api/user/delete/";
  var table_reset_url = "${ctx}/admin/user/reset/";
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
          { field: 'loginName', title: '账号', minWidth: 80, sort: true },
          { field: 'name', title: '姓名', minWidth: 100 },
          { field: 'sex', title: '性别', width: 70, templet: '#sexTpl' },
          { field: 'birthday', title: '出生日期', width: 110 },
          { field: 'mobile', title: '手机', width: 120 },
          { field: 'email', title: '邮箱', width: 200 },
          { field: 'status', title: '状态', width: 80, templet: '#statusTpl' },
          { fixed: 'right', title: '操作', width: 190, align: 'center', toolbar: '#kit-table-bar' }
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
      $('.kit-search-mored').hide();
      //带条件查询
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
      } else if (layEvent === 'del') { //停用
        layer.confirm('你确定要停用吗？', function (index) {
          layer.close(index);
          $.ajax({
            url: table_del_url + data.id,
            type: 'GET',
            cache: false,
            dataType: 'json',
            success: function (result) {
              if (result.success) {
                layer.msg('停用成功', {icon: 1});
                kitTable.reload();
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
          title: "修改用户",
          type: 2,
          content: table_edit_url + data.id,
          success: function(layero, index){
            setTimeout(function(){
              layui.layer.tips('点击此处返回用户列表', '.layui-layer-setwin .layui-layer-close', {
                tips: 3
              });
            },1000)
          },
          end: function() {
            kitTable.reload();
          }
        })
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
          layer.full(index);
        })
        layer.full(index);
      } else if (layEvent === 'reset') { //密码
        var index = layer.open({
          title: "修改密码",
          type: 2,
          content: table_reset_url + data.id,
          success: function(layero, index){
            setTimeout(function(){
              layui.layer.tips('点击此处返回用户列表', '.layui-layer-setwin .layui-layer-close', {
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
            title: "新增用户",
            type: 2,
            content: table_add_url,
            success: function(layero, index){
              setTimeout(function(){
                layui.layer.tips('点击此处返回用户列表', '.layui-layer-setwin .layui-layer-close', {
                  tips: 3
                });
              },1000)
            },
            end: function() {
              kitTable.reload();
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
</script>
</html>