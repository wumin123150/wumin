<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>分配用户</title>
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
    .select2-drop-active {
      z-index: 999999999;
    }
  </style>
</head>
<body>
<div class="kit-table">
  <form class="layui-form" lay-filter="kit-search-form">
    <div class="kit-table-header">
      <div class="kit-search-btns">
        <a href="javascript:;" data-action="add" class="layui-btn layui-btn-sm"><i class="layui-icon">&#xe608;</i>添加</a>
      </div>
      <div class="kit-search-inputs">
        <div class="kit-search-keyword">
          <input type="text" class="layui-input" name="searchKey" placeholder="搜索关键字.." />
          <button lay-submit lay-filter="search"><i class="layui-icon">&#xe615;</i></button>
        </div>
      </div>
    </div>
  </form>
  <div class="kit-table-body">
    <table id="kit-table" lay-filter="kit-table"></table>
    <script type="text/html" id="kit-table-bar">
      <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">移除</a>
    </script>
  </div>
</div>
<div id="dialog-container" class="layui-hide">
  <form class="layui-form layui-form-pane" action="${ctx}/api/membership/create" method="post" style="padding: 15px;">
    <input type="hidden" name="groupId" value="${groupId}"/>
    <div class="layui-form-item">
      <label class="layui-form-label">用户<span class="input-required">*</span></label>
      <div class="layui-input-block">
        <input type="text" name="userId" id="userId" class="select2"/>
      </div>
    </div>
  </form>
</div>
<script src="${ctx}/static/js/layui/layui.js"></script>
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script src="${ctx}/static/js/select2.min.js"></script>
</body>
<script>
  var tableId = 'kit-table';
  var tableFilter = 'kit-table';
  var table_page_url = "${ctx}/api/membership/page2?groupId=${groupId}";
  var table_del_url = "${ctx}/api/membership/delete";
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
          { field: 'loginName', title: '账号', width: 100, sort: true },
          { field: 'name', title: '名称', width: 150 },
          { field: 'mobile', title: '手机', width: 120 },
          { field: 'email', title: '邮箱', width: 200 },
          { fixed: 'right', title: '操作', width: 70, align: 'center', toolbar: '#kit-table-bar' }
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
        layer.confirm('你确定要移除吗？', function (index) {
          layer.close(index);
          $.ajax({
            url: table_del_url,
            type: 'POST',
            data: {
              userId: data.id,
              groupId: ${groupId}
            },
            cache: false,
            dataType: 'json',
            success: function (result) {
              if (result.success) {
                layer.msg('移除成功', {icon: 1});
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
        //do somehing
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
          layer.open({
            type: 1,
            title: "分配用户",
            shade: 0,
            move: false,
            offset: '50px',
            area: ['500px', '240px'],
            content: $('#dialog-container').html(),
            btn: [ '确认', '取消' ],
            btnAlign: 'c',
            success: function() {
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
            },
            cancel: function(index, layer) {
              $('.select2').select2('destroy');
            },
            btn1: function(index, layero) {
              var userId = layero.find('#userId').val();
              var loadIndex = layer.load();
              $.ajax({
                url: '${ctx}/api/membership/create',
                type: 'POST',
                cache: false,
                data: {userId:userId, groupId:${groupId}},
                dataType: 'json',
                success: function (result) {
                  if (result.success) {
                    loadIndex && layer.close(loadIndex);
                    layer.msg('保存成功', {icon: 1});
                    $('.select2').select2('destroy');
                    table.reload(tableId);
                    layer.close(index);
                  } else {
                    loadIndex && layer.close(loadIndex);
                    layer.msg(result.message, {icon: 2});
                  }
                },
                error: function () {
                  loadIndex && layer.close(loadIndex);
                  layer.msg('未知错误，请联系管理员', {icon: 5});
                }
              });
              return false;
            },
            btn2: function(index, layero) {
              $('.select2').select2('destroy');
              layer.close(index);
            }
          });
          break;
        case 'batchDel':
          break;
      }
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
  });
</script>
</html>