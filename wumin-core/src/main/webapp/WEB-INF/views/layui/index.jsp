<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <title>${iApplication}</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
  <meta http-equiv="Access-Control-Allow-Origin" content="*">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="format-detection" content="telephone=no">
  <!--
  <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
  -->
  <link rel="stylesheet" href="${ctx}/static/js/layui/css/layui.css" media="all"/>
  <link rel="stylesheet" href="//at.alicdn.com/t/font_470259_i5hllb8man61or.css" media="all" />
  <link rel="stylesheet" href="${ctx}/static/js/layui/main.css" media="all"/>
  <style>

  </style>
</head>
<body class="main_body">
<div class="layui-layout layui-layout-admin">
  <!-- 顶部 -->
  <div class="layui-header header">
    <div class="layui-main">
      <a href="#" class="logo">layui后台管理</a>
      <!-- 显示/隐藏菜单 -->
      <a href="javascript:;" class="iconfont hideMenu icon-menu"></a>
      <ul class="layui-nav top_menu">
        <li class="layui-nav-item">
          <a href="javascript:;">其它系统<span class="layui-badge-dot"></span></a>
          <dl class="layui-nav-child">
            <dd><a href="javascript:;">邮件管理</a></dd>
            <dd><a href="javascript:;">消息管理</a></dd>
            <dd><a href="javascript:;">授权管理</a></dd>
          </dl>
        </li>
        <li class="layui-nav-item" mobile>
          <a href="javascript:;" class="mobileAddTab" data-url="${ctx}/admin/changeAccount"><i class="iconfont icon-shezhi" data-icon="icon-shezhi"></i><cite>设置</cite></a>
        </li>
        <li class="layui-nav-item" mobile>
          <a href="${ctx}/logout" class="signOut"><i class="iconfont icon-zhuxiao"></i> 退出</a>
        </li>
        <li class="layui-nav-item screenfull" pc>
          <a href="javascript:;"><i class="iconfont icon-full"></i><cite>全屏</cite></a>
        </li>
        <li class="layui-nav-item screenlock" pc>
          <a href="javascript:;"><i class="iconfont icon-lock"></i><cite>锁屏</cite></a>
        </li>
        <li class="layui-nav-item" pc>
          <a href="javascript:;">
            <img src="${ctx}/avatar/<shiro:principal property="id"/>" onerror="this.src='${ctx}/static/avatars/avatar6.jpg'" class="layui-circle" width="35" height="35">
            <cite><shiro:principal property="name"/></cite>
          </a>
          <dl class="layui-nav-child">
            <dd><a href="javascript:;" data-url="${ctx}/admin/changeAccount"><i class="iconfont icon-zhanghu" data-icon="icon-zhanghuxinxi"></i><cite>个人资料</cite></a></dd>
            <dd><a href="javascript:;" class="changeSkin"><i class="iconfont icon-huanfu"></i><cite>更换皮肤</cite></a></dd>
            <dd><a href="${ctx}/logout" class="signOut"><i class="iconfont icon-zhuxiao"></i><cite>退出</cite></a></dd>
          </dl>
        </li>
      </ul>
    </div>
  </div>

  <!-- 左侧导航 -->
  <div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
      <div class="user-photo">
        <a class="img" title="我的头像" ><img src="${ctx}/avatar/<shiro:principal property="id"/>" onerror="this.src='${ctx}/static/avatars/avatar6.jpg'"></a>
        <p>您好！<span class="userName"><shiro:principal property="name"/></span></p>
      </div>
      <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
      <ul class="layui-nav layui-nav-tree" lay-filter="kitNavbar" kit-navbar>
        <li class="layui-nav-item layui-nav-itemed">
          <a class="" href="javascript:;"><i class="layui-icon">&#xe613;</i><span>人员管理</span></a>
          <dl class="layui-nav-child">
            <dd>
              <a href="javascript:;" data-url="${ctx}/admin/user"><i class="layui-icon" data-icon="&#xe612;">&#xe612;</i><cite>用户</cite></a>
            </dd>
            <dd>
              <a href="javascript:;" data-url="${ctx}/admin/userGroup"><cite>机构</cite></a>
            </dd>
            <dd>
              <a href="javascript:;" data-url="${ctx}/admin/role"><cite>角色</cite></a>
            </dd>
          </dl>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
          <a href="javascript:;" data-url="${ctx}/admin/config"><i class="layui-icon">&#xe614;</i><cite>系统配置</cite></a>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
          <a href="javascript:;" data-url="${ctx}/admin/dataType"><i class="layui-icon">&#xe62d;</i><cite>字典</cite></a>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
          <a href="javascript:;" data-url="${ctx}/admin/message"><i class="layui-icon">&#xe63a;</i><cite>消息</cite></a>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
          <a href="javascript:;" data-url="${ctx}/admin/annex"><i class="layui-icon">&#xe621;</i><cite>附件</cite></a>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
          <a href="javascript:;" data-url="${ctx}/admin/log"><i class="layui-icon">&#xe705;</i><cite>日志</cite></a>
        </li>
      </ul>
    </div>
  </div>
  <div class="layui-body layui-form">
    <div class="layui-tab layui-tab-card" id="top_tabs_box" lay-filter="bodyTab">
      <!-- 选项卡 -->
      <ul class="layui-tab-title top_tab" id="top_tabs">
        <li class="layui-this" lay-id=""><i class="iconfont icon-home"></i> 后台首页</li>
      </ul>
      <ul class="layui-nav closeBox">
        <li class="layui-nav-item">
          <a href="javascript:;"><i class="iconfont icon-caozuo"></i> 页面操作</a>
          <dl class="layui-nav-child">
            <dd><a href="javascript:;" class="refresh refreshThis"><i class="iconfont icon-refresh"></i> 刷新当前</a></dd>
            <dd><a href="javascript:;" class="closePageOther"><i class="iconfont icon-jujue"></i> 关闭其他</a></dd>
            <dd><a href="javascript:;" class="closePageAll"><i class="iconfont icon-close"></i> 关闭全部</a></dd>
          </dl>
        </li>
      </ul>
      <div class="layui-tab-content clildFrame">
        <div class="layui-tab-item layui-show layui-anim layui-anim-upbit">
          <iframe src="${ctx}/admin/home"></iframe>
        </div>
      </div>
    </div>
  </div>

  <div class="layui-footer footer">
    <div class="layui-main">
    2017 &copy; <a href="http://www.foomei.com/">www.foomei.com</a> MIT license
    </div>
  </div>
</div>

<!-- 移动导航 -->
<div class="site-tree-mobile layui-hide"><i class="layui-icon">&#xe602;</i></div>
<div class="site-mobile-shade"></div>

<script src="${ctx}/static/js/layui/layui.js"></script>
<script src="${ctx}/static/js/layui/leftNav.js"></script>
<script src="${ctx}/static/js/screenfull.min.js"></script>
<script>
  var $,tab;
  layui.config({
    base: "${ctx}/static/js/layui/"
  }).use(['bodyTab','form','element','layer','jquery'],function(){
    var form = layui.form,
      layer = layui.layer,
      element = layui.element;
    $ = layui.jquery;
    tab = layui.bodyTab({
      openTabNum: "50"  //最大可打开窗口数量
    });

    //更换皮肤
    function skins(){
      var skin = window.sessionStorage.getItem("skin");
      if(skin){  //如果更换过皮肤
        if(window.sessionStorage.getItem("skinValue") != "自定义"){
          $("body").addClass(window.sessionStorage.getItem("skin"));
        }else{
          $(".layui-layout-admin .layui-header").css("background-color",skin.split(',')[0]);
          $(".layui-bg-black").css("background-color",skin.split(',')[1]);
        }
      }
    }
    skins();
    $(".changeSkin").click(function(){
      layer.open({
        title: "更换皮肤",
        area: ["310px","240px"],
        type: "1",
        content: '<div class="skins_box">'+
        '<form class="layui-form">'+
        '<div class="layui-form-item">'+
        '<input type="radio" name="skin" value="默认" title="默认" lay-filter="default" checked="">'+
        '<input type="radio" name="skin" value="橙色" title="橙色" lay-filter="orange">'+
        '<input type="radio" name="skin" value="蓝色" title="蓝色" lay-filter="blue">'+
        '<input type="radio" name="skin" value="自定义" title="自定义" lay-filter="custom">'+
        '<div class="skinCustom">'+
        '<input type="text" class="layui-input topColor" name="topSkin" placeholder="顶部颜色" />'+
        '<input type="text" class="layui-input leftColor" name="leftSkin" placeholder="左侧颜色" />'+
        '</div>'+
        '</div>'+
        '<div class="layui-form-item skinBtn">'+
        '<a href="javascript:;" class="layui-btn layui-btn-sm layui-btn-normal" lay-submit="" lay-filter="changeSkin">确定更换</a>'+
        '<a href="javascript:;" class="layui-btn layui-btn-sm layui-btn-primary" lay-submit="" lay-filter="noChangeSkin">我再想想</a>'+
        '</div>'+
        '</form>'+
        '</div>',
        success: function(index, layero){
          var skin = window.sessionStorage.getItem("skin");
          if(window.sessionStorage.getItem("skinValue")){
            $(".skins_box input[value="+window.sessionStorage.getItem("skinValue")+"]").attr("checked","checked");
          };
          if($(".skins_box input[value=自定义]").attr("checked")){
            $(".skinCustom").css("visibility","inherit");
            $(".topColor").val(skin.split(',')[0]);
            $(".leftColor").val(skin.split(',')[1]);
          };
          form.render();
          $(".skins_box").removeClass("layui-hide");
          $(".skins_box .layui-form-radio").on("click",function(){
            var skinColor;
            if($(this).find("span").text() == "橙色"){
              skinColor = "orange";
            }else if($(this).find("span").text() == "蓝色"){
              skinColor = "blue";
            }else if($(this).find("span").text() == "默认"){
              skinColor = "";
            }
            if($(this).find("span").text() != "自定义"){
              $(".topColor,.leftColor").val('');
              $("body").removeAttr("class").addClass("main_body "+skinColor+"");
              $(".skinCustom").removeAttr("style");
              $(".layui-bg-black,.layui-layout-admin .layui-header").removeAttr("style");
            }else{
              $(".skinCustom").css("visibility","inherit");
            }
          })
          var skinStr,skinColor;
          $(".topColor").blur(function(){
            $(".layui-layout-admin .layui-header").css("background-color",$(this).val());
          })
          $(".leftColor").blur(function(){
            $(".layui-bg-black").css("background-color",$(this).val());
          })

          form.on("submit(changeSkin)",function(data){
            if(data.field.skin != "自定义"){
              if(data.field.skin == "橙色"){
                skinColor = "orange";
              }else if(data.field.skin == "蓝色"){
                skinColor = "blue";
              }else if(data.field.skin == "默认"){
                skinColor = "";
              }
              window.sessionStorage.setItem("skin",skinColor);
            }else{
              skinStr = $(".topColor").val()+','+$(".leftColor").val();
              window.sessionStorage.setItem("skin",skinStr);
              $("body").removeAttr("class").addClass("main_body");
            }
            window.sessionStorage.setItem("skinValue",data.field.skin);
            layer.closeAll("page");
          });
          form.on("submit(noChangeSkin)",function(){
            $("body").removeAttr("class").addClass("main_body "+window.sessionStorage.getItem("skin")+"");
            $(".layui-bg-black,.layui-layout-admin .layui-header").removeAttr("style");
            skins();
            layer.closeAll("page");
          });
        },
        cancel: function(){
          $("body").removeAttr("class").addClass("main_body "+window.sessionStorage.getItem("skin")+"");
          $(".layui-bg-black,.layui-layout-admin .layui-header").removeAttr("style");
          skins();
        }
      })
    })

    //退出
    $(".signOut").click(function(){
      window.sessionStorage.removeItem("menu");
      menu = [];
      window.sessionStorage.removeItem("curmenu");
    })

    //隐藏左侧导航
    $(".hideMenu").click(function(){
      $(".layui-layout-admin").toggleClass("showMenu");
      //渲染顶部窗口
      tab.tabMove();
    })

    //渲染左侧菜单
    tab.render();

    //全屏
    $(".screenfull").on("click",function(){
      if (!screenfull.enabled) {
        layer.msg("浏览器不支持！");
        return false
      }
      screenfull.request();
    })

    //锁屏
    function lockPage(){
      layer.open({
        title: false,
        type: 1,
        content: '	<div class="admin-header-lock" id="lock-box">'+
        '<div class="admin-header-lock-img"><img src="${ctx}/avatar/<shiro:principal property="id"/>" onerror="this.src=\'${ctx}/static/avatars/avatar6.jpg\'"></div>'+
        '<div class="admin-header-lock-name" id="lockUserName"><shiro:principal property="name"/></div>'+
        '<div class="input_btn">'+
        '<input type="password" class="admin-header-lock-input layui-input" autocomplete="off" placeholder="请输入密码锁屏.." name="lockPwd" id="lockPwd" />'+
        '<button class="layui-btn" id="lock">锁屏</button>'+
        '</div>'+
        '</div>',
        closeBtn: 0,
        shade: 0.9
      })
      $(".admin-header-lock-input").focus();
    }
    function unlockPage(){
      layer.open({
        title: false,
        type: 1,
        content: '	<div class="admin-header-lock" id="lock-box">'+
        '<div class="admin-header-lock-img"><img src="${ctx}/avatar/<shiro:principal property="id"/>" onerror="this.src=\'${ctx}/static/avatars/avatar6.jpg\'"></div>'+
        '<div class="admin-header-lock-name" id="lockUserName"><shiro:principal property="name"/></div>'+
        '<div class="input_btn">'+
        '<input type="password" class="admin-header-lock-input layui-input" autocomplete="off" placeholder="请输入密码解锁.." name="unlockPwd" id="unlockPwd" />'+
        '<button class="layui-btn" id="unlock">解锁</button>'+
        '</div>'+
        '</div>',
        closeBtn: 0,
        shade: 0.9
      })
      $(".admin-header-lock-input").focus();
    }
    $(".screenlock").on("click",function(){
      window.sessionStorage.setItem("screenlock",true);
      if(window.sessionStorage.getItem("screenlockPwd") == null) {
        lockPage();
      } else {
        unlockPage();
      }
    })
    // 判断是否显示锁屏
    if(window.sessionStorage.getItem("screenlock") == "true"){
      unlockPage();
    }
    // 锁屏
    $("body").on("click","#lock",function(){
      if($(this).siblings(".admin-header-lock-input").val() == ''){
        layer.msg("请输入锁屏密码！");
        $(this).siblings(".admin-header-lock-input").focus();
      }else{
        window.sessionStorage.setItem("screenlockPwd",$(this).siblings(".admin-header-lock-input").val());
        unlockPage();
      }
    });
    // 解锁
    $("body").on("click","#unlock",function(){
      if($(this).siblings(".admin-header-lock-input").val() == ''){
        layer.msg("请输入解锁密码！", {icon: 2});
        $(this).siblings(".admin-header-lock-input").focus();
      }else{
        if($(this).siblings(".admin-header-lock-input").val() == window.sessionStorage.getItem("screenlockPwd")){
          window.sessionStorage.setItem("screenlock",false);
          $(this).siblings(".admin-header-lock-input").val('');
          layer.closeAll("page");
        }else{
          layer.msg("密码错误，请重新输入！", {icon: 2});
          $(this).siblings(".admin-header-lock-input").val('').focus();
        }
      }
    });
    $(document).on('keydown', function() {
      if(event.keyCode == 13) {
        $("#unlock").click();
      }
    });

    //手机设备的简单适配
    var treeMobile = $('.site-tree-mobile'),
      shadeMobile = $('.site-mobile-shade')

    treeMobile.on('click', function(){
      $('body').addClass('site-mobile');
    });

    shadeMobile.on('click', function(){
      $('body').removeClass('site-mobile');
    });

    // 添加新窗口
    $("body").on("click",".layui-nav .layui-nav-item a",function(){
      //如果不存在子级
      if($(this).siblings().length == 0){
        addTab($(this));
        $('body').removeClass('site-mobile');  //移动端点击菜单关闭菜单层
      }
      $(this).parent("li").siblings().removeClass("layui-nav-itemed");
    })

    //刷新后还原打开的窗口
    if(window.sessionStorage.getItem("menu") != null){
      menu = JSON.parse(window.sessionStorage.getItem("menu"));
      curmenu = window.sessionStorage.getItem("curmenu");
      var openTitle = '';
      for(var i=0;i<menu.length;i++){
        openTitle = '';
        if(menu[i].icon){
          if(menu[i].icon.split("-")[0] == 'icon'){
            openTitle += '<i class="iconfont '+menu[i].icon+'"></i>';
          }else{
            openTitle += '<i class="layui-icon">'+menu[i].icon+'</i>';
          }
        }
        openTitle += '<cite>'+menu[i].title+'</cite>';
        openTitle += '<i class="layui-icon layui-unselect layui-tab-close" data-id="'+menu[i].layId+'">&#x1006;</i>';
        element.tabAdd("bodyTab",{
          title: openTitle,
          content: "<iframe src='"+menu[i].href+"' data-id='"+menu[i].layId+"'></frame>",
          id: menu[i].layId
        })
        //定位到刷新前的窗口
        if(curmenu != "undefined"){
          if(curmenu == '' || curmenu == "null"){  //定位到后台首页
            element.tabChange("bodyTab",'');
          }else if(JSON.parse(curmenu).title == menu[i].title){  //定位到刷新前的页面
            element.tabChange("bodyTab",menu[i].layId);
          }
        }else{
          element.tabChange("bodyTab",menu[menu.length-1].layId);
        }
      }
      //渲染顶部窗口
      tab.tabMove();
    }

    //刷新当前
    $(".refresh").on("click",function(){  //此处添加禁止连续点击刷新一是为了降低服务器压力，另外一个就是为了防止超快点击造成chrome本身的一些js文件的报错(不过貌似这个问题还是存在，不过概率小了很多)
      if($(this).hasClass("refreshThis")){
        $(this).removeClass("refreshThis");
        $(".clildFrame .layui-tab-item.layui-show").find("iframe")[0].contentWindow.location.reload(true);
        setTimeout(function(){
          $(".refresh").addClass("refreshThis");
        },2000)
      }else{
        layer.msg("您点击的速度超过了服务器的响应速度，还是等两秒再刷新吧！");
      }
    })

    //关闭其他
    $(".closePageOther").on("click",function(){
      if($("#top_tabs li").length>2 && $("#top_tabs li.layui-this cite").text()!="后台首页"){
        var menu = JSON.parse(window.sessionStorage.getItem("menu"));
        $("#top_tabs li").each(function(){
          if($(this).attr("lay-id") != '' && !$(this).hasClass("layui-this")){
            element.tabDelete("bodyTab",$(this).attr("lay-id")).init();
            //此处将当前窗口重新获取放入session，避免一个个删除来回循环造成的不必要工作量
            for(var i=0;i<menu.length;i++){
              if($("#top_tabs li.layui-this cite").text() == menu[i].title){
                menu.splice(0,menu.length,menu[i]);
                window.sessionStorage.setItem("menu",JSON.stringify(menu));
              }
            }
          }
        })
      }else if($("#top_tabs li.layui-this cite").text()=="后台首页" && $("#top_tabs li").length>1){
        $("#top_tabs li").each(function(){
          if($(this).attr("lay-id") != '' && !$(this).hasClass("layui-this")){
            element.tabDelete("bodyTab",$(this).attr("lay-id")).init();
            window.sessionStorage.removeItem("menu");
            menu = [];
            window.sessionStorage.removeItem("curmenu");
          }
        })
      }else{
        layer.msg("没有可以关闭的窗口了@_@");
      }
      //渲染顶部窗口
      tab.tabMove();
    })
    //关闭全部
    $(".closePageAll").on("click",function(){
      if($("#top_tabs li").length > 1){
        $("#top_tabs li").each(function(){
          if($(this).attr("lay-id") != ''){
            element.tabDelete("bodyTab",$(this).attr("lay-id")).init();
            window.sessionStorage.removeItem("menu");
            menu = [];
            window.sessionStorage.removeItem("curmenu");
          }
        })
      }else{
        layer.msg("没有可以关闭的窗口了@_@");
      }
      //渲染顶部窗口
      tab.tabMove();
    })
  })

  //打开新窗口
  function addTab(_this){
    tab.tabAdd(_this);
  }
</script>
</body>
</html>