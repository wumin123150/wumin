<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<style>
  .navbar-toggle {
    margin-top: 0px;
    margin-bottom: 0px;
  }

  .navbar-inverse .navbar-toggle {
    border-color: #b8b8b8;
    background-color: #0a9d4b;
  }

  .navbar-inverse .navbar-toggle:focus, .navbar-inverse .navbar-toggle:hover {
    background-color: #0a9d4b;
  }

  .navbar-inverse {
    background-color: #f9f9f9;
    border-color: #eee;
  }

  .navbar {
    min-height: 30px;
  }

  .navbar-brand {
    height: 32px;
    padding: 6px 15px;
    font-size: 14px;
  }

  .navbar-inverse .navbar-brand:focus, .navbar-inverse .navbar-brand:hover {
    color: #0a9d4b;
  }

  .navbar-inverse .navbar-nav > li > a {
    color: #9d9d9d;
  }

  .navbar-inverse .navbar-nav > li > a:hover, .navbar-inverse .navbar-nav > li > a:focus {
    color: #0a9d4b;
  }

  .navbar-inverse .navbar-nav > .open > a, .navbar-inverse .navbar-nav > .open > a:hover, .navbar-inverse .navbar-nav > .open > a:focus {
    background-color: #fff;
    color: #0a9d4b;
  }

  .dropdown-menu > li > a:hover, .dropdown-menu > li > a:focus {
    color: #0a9d4b;
  }

  .dropdown-menu.dropdown-menu-right:before {
    left: auto;
    right: 9px;
  }

  .dropdown-menu:before {
    border-bottom: 7px solid rgba(0, 0, 0, .2);
    -moz-border-bottom-colors: rgba(0, 0, 0, .2);
    border-left: 7px solid transparent;
    border-right: 7px solid transparent;
    content: "";
    display: inline-block;
    left: 9px;
    position: absolute;
    top: -7px;
  }

  .nav li.top_line {
    height: 32px;
    padding-top: 6px;
  }

  .nav li.top_line span {
    background: url(${ctx}/static/images/subnav_line.gif) repeat-y center;
  }

  @media ( min-width: 992px) {
    .navbar-nav > li > a {
      padding-top: 6px;
      padding-bottom: 6px;
    }
  }

  @media ( min-width: 768px) {
    .navbar-nav > li > a {
      padding-top: 6px;
      padding-bottom: 6px;
    }
  }

  @media ( max-width: 768px) {
    .nav li.top_line {
      display: none;
    }
  }

  .search_suggest > ul {
    list-style-type: none;
    padding: 0px;
    margin: 0px;
  }

  .search_suggest > ul > li {
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 36px;
    padding-right: 36px;
    cursor: pointer;

  }

  .search_suggest {
    float: left;
    position: absolute;
    border: 1px solid #ddd;
    background-color: white;
    top: 39px;
    left: 68px;
    display: none;
    margin-left: -69px;
    width: 380px;
    z-index: 50;
    max-height: 300px;
    overflow-y: auto;
  }

  .dropdown-toggle.btn-search {
    background-color: #ffffff !important;
  }

  .dropdown-menu.search-select:before {
    content: none;
  }

  .btn:active {
    box-shadow: none;
  }

  .dropdown-menu.search-select {
    box-shadow: none;
    min-width: 68px;
    padding: 0px;
    border: 1px solid rgb(0, 153, 68);
    border-radius: 0px;
    border-top: 0px;
    margin-top: -0.5px;
    margin-left: -1px !important;

  }

  .dropdown-menu.search-select > li > a {
    padding: 3px 10px;
    font-size: 16px;
  }

  .dropdown-toggle.btn-search:before {
    border: none;
  }

  .search.input-group {
    margin: 0 auto;
    border: 1px solid #009944;
    padding: 0px;
    width: 480px;
  }

  .search.input-group input {
    padding: 8px 7px 8px 7px;
    font: 16px arial;
    border: none;
    vertical-align: top;
    outline: none;
    box-shadow: none;
    height: 38px;
  }

  .search.input-group .btn {
    border-radius: 0;
    background-color: #009944;
    font-size: 16px;
    padding: 8px 12px;
    border: 0px;
    height: auto !important;
    margin-right: 0px !important;
  }

  .navbar-fixed-top + .container {
    padding-top: 0px;
  }

  a.dropdown-toggle.btn-search.disabled {
    opacity: 1;
  }

  .hover {
    background-color: #eee
  }
</style>
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#bs-navbar">
        <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <span class="navbar-brand"> <span id="navbar-username"> <shiro:principal property="name"/>
      </span>您好，欢迎来到${iApplication}
      </span>
    </div>
    <div id="bs-navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav navbar-right">
        <li><a href="${ctx}/" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', ''])">首页</a></li>
        <shiro:guest>
          <li class="top_line" id="_nav_login_line"><span>&nbsp;</span></li>
          <li id="_nav_login">
            <a href="${ctx}/login" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'login'])">登录</a>
          </li>
          <li class="top_line" id="_nav_register_line"><span>&nbsp;</span></li>
          <li id="_nav_register">
            <a href="${ctx}/register" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'register'])">注册</a>
          </li>
        </shiro:guest>
        <shiro:user>
          <li class="top_line"><span>&nbsp;</span></li>
          <shiro:hasRole name="admin">
            <a href="${ctx}/admin/index">管理后台</a>&nbsp;&nbsp;
          </shiro:hasRole>
          <li class="top_line"><span>&nbsp;</span></li>
          <li id="_nav_logout">
            <a href="${ctx}/logout" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'logout'])">
              <i class="ace-icon fa fa-power-off"></i>退出
            </a>
          </li>
        </shiro:user>
      </ul>
    </div>
  </div>
</div>