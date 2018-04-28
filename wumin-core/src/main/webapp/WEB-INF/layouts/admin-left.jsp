<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://www.foomei.com/tags/shiro" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!-- #section:basics/sidebar -->
<div id="sidebar" class="sidebar responsive">
  <script type="text/javascript">
    try {
      ace.settings.check('sidebar', 'fixed')
    } catch (e) {
    }
  </script>

  <div class="sidebar-shortcuts" id="sidebar-shortcuts">
    <div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
      <!-- #section:basics/sidebar.layout.shortcuts -->
      <a class="btn btn-success" href="${ctx}/admin/index">
        <i class="ace-icon fa fa-home"></i>
      </a>

      <a class="btn btn-info" href="${ctx}/admin/user">
        <i class="ace-icon fa fa-user"></i>
      </a>

      <a class="btn btn-warning" href="${ctx}/admin/userGroup">
        <i class="ace-icon fa fa-university"></i>
      </a>

      <a class="btn btn-danger" href="${ctx}/admin/config/view">
        <i class="ace-icon fa fa-cogs"></i>
      </a>

      <!-- /section:basics/sidebar.layout.shortcuts -->
    </div>

    <div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
      <span class="btn btn-success"></span>

      <span class="btn btn-info"></span>

      <span class="btn btn-warning"></span>

      <span class="btn btn-danger"></span>
    </div>
  </div><!-- /.sidebar-shortcuts -->

  <ul class="nav nav-list">
    <li class="<c:if test='${menu == "user" || menu == "userGroup" || menu == "role" }'>active open</c:if>">
      <a href=‘#’ class="dropdown-toggle">
        <i class="menu-icon fa fa-user"></i>
        <span class="menu-text">人员管理</span>
        <b class="arrow fa fa-angle-down"></b>
      </a>
      <ul class="submenu nav-show">
        <li class="<c:if test='${menu == "user"}'>active</c:if>">
          <a href="${ctx}/admin/user">
            <i class="menu-icon fa fa-caret-right"></i>
            <span class="menu-text"> 用户 </span>
          </a>

          <b class="arrow"></b>
        </li>
        <li class="<c:if test='${menu == "userGroup"}'>active</c:if>">
          <a href="${ctx}/admin/userGroup">
            <i class="menu-icon fa fa-caret-right"></i>
            <span class="menu-text"> 机构 </span>
          </a>

          <b class="arrow"></b>
        </li>
        <li class="<c:if test='${menu == "role"}'>active</c:if>">
          <a href="${ctx}/admin/role">
            <i class="menu-icon fa fa-caret-right"></i>
            <span class="menu-text"> 角色 </span>
          </a>

          <b class="arrow"></b>
        </li>
      </ul>
    </li>
    <li class="<c:if test='${menu == "configs"}'>active</c:if>">
      <a href="${ctx}/admin/config/view">
        <i class="menu-icon fa fa-cogs"></i>
        <span class="menu-text"> 系统设置 </span>
      </a>

      <b class="arrow"></b>
    </li>
    <li class="<c:if test='${menu == "config"}'>active</c:if>">
      <a href="${ctx}/admin/config">
        <i class="menu-icon fa fa-cog"></i>
        <span class="menu-text"> 系统配置 </span>
      </a>

      <b class="arrow"></b>
    </li>
    <li class="<c:if test='${menu == "dataDictionary"}'>active</c:if>">
      <a href="${ctx}/admin/dataType">
        <i class="menu-icon fa fa-table"></i>
        <span class="menu-text"> 字典 </span>
      </a>

      <b class="arrow"></b>
    </li>
    <li class="<c:if test='${menu == "message"}'>active</c:if>">
      <a href="${ctx}/admin/message">
        <i class="menu-icon fa fa-envelope"></i>
        <span class="menu-text"> 消息 </span>
      </a>

      <b class="arrow"></b>
    </li>
    <li class="<c:if test='${menu == "annex"}'>active</c:if>">
      <a href="${ctx}/admin/annex">
        <i class="menu-icon fa fa-file-text"></i>
        <span class="menu-text"> 附件 </span>
      </a>

      <b class="arrow"></b>
    </li>
    <li class="<c:if test='${menu == "log"}'>active</c:if>">
      <a href="${ctx}/admin/log">
        <i class="menu-icon fa fa-list"></i>
        <span class="menu-text"> 日志 </span>
      </a>

      <b class="arrow"></b>
    </li>

  </ul><!-- /.nav-list -->

  <!-- #section:basics/sidebar.layout.minimize -->
  <div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
    <i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left"
       data-icon2="ace-icon fa fa-angle-double-right"></i>
  </div>

  <!-- /section:basics/sidebar.layout.minimize -->
  <script type="text/javascript">
    try {
      ace.settings.check('sidebar', 'collapsed')
    } catch (e) {
    }
  </script>
</div>