<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
  <title>重置密码</title>
  <!--
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
  <meta http-equiv="Cache-Control" content="no-store" />
  <meta http-equiv="Pragma" content="no-cache" />
  <meta http-equiv="Expires" content="0" />
   -->
  <meta charset="utf-8"/>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
  <meta http-equiv="Access-Control-Allow-Origin" content="*">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

  <!--
  <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
  -->

  <!-- bootstrap & fontawesome -->
  <link rel="stylesheet" href="${ctx}/webjars/bootstrap/css/bootstrap.min.css"/>
  <link rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>

  <link rel="stylesheet" href="${ctx}/static/css/toastr.min.css"/>

  <!-- text fonts -->
  <link rel="stylesheet" href="${ctx}/static/css/ace-fonts.min.css"/>

  <!-- ace styles -->
  <link rel="stylesheet" href="${ctx}/static/css/ace.min.css" class="ace-main-stylesheet" id="main-ace-style"/>

  <!--[if lte IE 9]>
  <link rel="stylesheet" href="${ctx}/static/css/ace-part2.min.css" class="ace-main-stylesheet"/>
  <![endif]-->

  <!--[if lte IE 9]>
  <link rel="stylesheet" href="${ctx}/static/css/ace-ie.min.css"/>
  <![endif]-->

  <!-- inline styles related to this page -->
  <style>
    #message-info {
      margin-bottom: 15px;
    }

    #error-info {
      margin-bottom: 15px;
    }

    .no-skin .login-layout {
      background: none;
      background-color: #f9f9f9;
    }

    .login-box .toolbar {
      background: #76B774;
      border-top: 2px solid #759759;
    }

    html, body {
      background-color: #f9f9f9;
    }

    .main-container:before {
      background-color: #f9f9f9;
    }

    .main-container.container:before {
      -webkit-box-shadow: none;
      box-shadow: none;
    }

    .footer .footer-inner .footer-content {
      line-height: 18px;
      background-color: #f9f9f9;
    }

    @media only screen and (max-width: 1024px) {
      .hidden-1024 {
        display: none !important;
      }
    }
  </style>

  <!-- ace settings handler -->
  <script src="${ctx}/static/js/ace-extra.min.js"></script>

  <!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->

  <!--[if lte IE 8]>
  <script src="${ctx}/static/js/html5shiv.min.js"></script>
  <script src="${ctx}/static/js/respond.min.js"></script>
  <![endif]-->
</head>
<body class="no-skin">
<div class="login-layout light-login main-container container">
  <div class="container">
    <div class="row">
      <div class="pull-left">
        <a title="${iApplication}" href="${ctx}/">
          <img width="280" height="53" class="lazy">
        </a>
      </div>
    </div>

    <div class="row" id="main" style="min-height: 420px;">
      <div class="col-sm-6 hidden-1024">
        <div class="space-32"></div>
        <img width="520" height="306" class="lazy">
      </div>
      <div class="space-8"></div>
      <div class="col-sm-6">
        <div class="login-container">
          <div id="login-box" class="login-box visible widget-box no-border">
            <div class="widget-body">
              <div class="widget-main">
                <h4 class="header green lighter bigger">
                  <i class="ace-icon fa fa-coffee green"></i> 重置密码
                </h4>

                <div class="space-6"></div>

                <form id="login-form" action="${ctx}/retrieve" method="post">
                  <fieldset>
                    <div class="form-group">
                      <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input id="username" name="username" value="${username}" type="text" class="form-control"
                                   placeholder="用户名"/>
														<i class="ace-icon fa fa-user"></i>
													</span>
                      </label>
                    </div>
                    <div class="form-group">
                      <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input id="captcha" name="captcha" type="text" class="form-control" placeholder="验证码"
                                   style="ime-mode:disabled; width:58%!important; display:inline!important;"
                                   autocomplete="off"/>
														<button id="captchaButton" type="button" class="btn btn-sm btn-primary"
                                    onclick="sendCaptcha();">
															<i class="ace-icon fa fa-envelope"></i>
															<span>获取验证码</span>
														</button>
													</span>
                      </label>
                    </div>
                    <div class="form-group">
                      <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input id="password" name="password" type="password" class="form-control"
                                   placeholder="新密码"/>
														<i class="ace-icon fa fa-lock"></i>
													</span>
                      </label>
                    </div>
                    <div class="form-group">
                      <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input id="repassword" name="repassword" type="password" class="form-control"
                                   placeholder="确认密码"/>
														<i class="ace-icon fa fa-retweet"></i>
													</span>
                      </label>
                    </div>
                    <div id="error-info" class="text-danger"><c:if test="${not empty error}"><i
                      class='fa fa-times-circle'></i>${error.defaultMessage}</c:if>&nbsp;</div>

                    <div class="clearfix">
                      <button type="submit" class="width-35 pull-right btn btn-sm btn-success">
                        <i class="ace-icon fa fa-lightbulb-o"></i>
                        <span class="bigger-110">重置密码</span>
                      </button>
                    </div>
                  </fieldset>
                </form>
              </div><!-- /.widget-main -->

              <div class="toolbar clearfix">
                <div>
                  <a href="${ctx}/login" data-target="#login-box" class="user-signup-link">
                    <i class="ace-icon fa fa-arrow-left"></i>
                    返回登录
                  </a>
                </div>
              </div>
            </div><!-- /.widget-body -->
          </div><!-- /.login-box -->
        </div><!-- /.position-relative -->
      </div>
    </div>
  </div>
</div>

<div class="footer">
  <div class="footer-inner">
    <!-- #section:basics/footer -->
    <div class="footer-content">
				<span>
					<p class="center">© ${iCompany}</p>
				</span>
    </div>

    <!-- /section:basics/footer -->
  </div>
</div>

<!-- basic scripts -->
<script src="${ctx}/webjars/jquery/jquery.min.js"></script>
<script src="${ctx}/webjars/bootstrap/js/bootstrap.min.js"></script>

<!-- page specific plugin scripts -->
<script src="${ctx}/static/js/jquery.validate.min.js"></script>
<script src="${ctx}/static/js/additional-methods.min.js"></script>
<script src="${ctx}/static/js/jquery.validate.foomei.js"></script>
<script src="${ctx}/static/js/messages_zh.min.js"></script>
<script src="${ctx}/static/js/toastr.min.js"></script>
<script>
  toastr.options = {
    closeButton: false,
    debug: false,
    newestOnTop: true,
    progressBar: true,
    positionClass: "toast-top-center",
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "2000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
  };
</script>

<!-- ace scripts -->
<script src="${ctx}/static/js/ace.min.js"></script>

<!-- inline scripts related to this page -->
<script type="text/javascript">
  function sendCaptcha() {
    var username = $('#username').val();

    if (username == null || username == "") {
      toastr.warning('请您先输入用户名');
      return;
    }

    if (!$('#login-form').validate().element($('#username'))) {
      return;
    }

    $.ajax({
      url: "${ctx}/api/captcha/send",
      data: 'phone=' + username,
      type: 'POST',
      cache: false,
      dataType: 'json',
      success: function (result) {
        if (result.success) {
          toastr.success('已发送验证码到您的手机，请查收');
          timeoutCaptcha();
        } else {
          toastr.error(result.message);
        }
      },
      error: function () {
        toastr.error('未知错误，请联系管理员');
      }
    });
  }

  function timeoutCaptcha(time) {
    if (time == null)
      time = 60;

    if (time > 0) {
      setTimeout(function () {
        time--;
        $("#captchaButton").find("span").text("重新发送(" + time + ")");
        $("#captchaButton").attr("disabled", "disabled");
        $("#captchaButton").removeClass("btn-primary");
        timeoutCaptcha(time);
      }, 1000);
    } else {
      $("#captchaButton").find("span").text("获取验证码");
      $("#captchaButton").removeAttr("disabled");
      $("#captchaButton").addClass("btn-primary");
    }
  }

  jQuery(function ($) {
    $('#login-form').validate({
      errorElement: 'div',
      focusInvalid: true,
      ignore: "",
      rules: {
        username: {
          required: true
        },
        captcha: {
          required: true,
          remote: {
            type: 'POST',
            url: '${ctx}/api/captcha/check',
            dataType: 'json',
            data: {
              phone: function () {
                return $('#username').val();
              },
              captcha: function () {
                return $('#captcha').val();
              }
            }
          }
        },
        password: {
          required: true,
          rangelength: [6, 16],
          pass: true
        },
        repassword: {
          equalTo: '#password'
        }
      },
      messages: {
        username: {
          required: '请您输入用户名'
        },
        captcha: {
          required: '请您输入验证码',
          remote: '验证码错误'
        },
        password: {
          required: '请您输入新密码',
          rangelength: '密码要求6~16个字符'
        },
        repassword: {
          equalTo: '必须与新密码保持一致'
        }
      },
      highlight: function (e) {
      },
      success: function (e) {
        if ($("#error-info > div").html() == "") {
          $("#error-info").empty().append("&nbsp;");
        }
      },
      showErrors: function (errorMap, errorList) {
        $(errorList).each(function (i, item) {
          item.message = ("<i class='fa fa-times-circle'></i>" + item.message);
        })

        this.defaultShowErrors();
      },
      errorPlacement: function (error, element) {
        if ($("#error-info > div").html() == "") {
          $("#error-info").empty().append("&nbsp;");
        }

        if ($("#error-info > .error").size() > 0) {
          return false;
        }

        $("#error-info").html(error);
      },
      submitHandler: function (form) {
        form.submit();
      },
      invalidHandler: function (form) {
      }
    });
  });
</script>
</body>
</html>