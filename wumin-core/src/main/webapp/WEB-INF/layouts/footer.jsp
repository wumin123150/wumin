<%@ page language="java" pageEncoding="UTF-8" %>
<style>
  .footer {
    padding-top: 75px;
  }

  .footer .footer-inner {
    text-align: center;
    position: absolute;
    z-index: auto;
    left: 0;
    right: 0;
    bottom: 0;
  }

  .footer .footer-inner .footer-content {
    position: absolute;
    left: 0px;
    right: 0px;
    bottom: 0px;
    padding: 8px;
    line-height: 36px;
    border-top: 1px solid #eee;
    background-color: rgba(51, 51, 51, 0.08);
  }
</style>
<footer id="footer" class="footer">
  <div class="footer-inner">
    <div class="footer-content">
      <span class="bigger-120">
        © ${iCompany}
      </span>
    </div>
  </div>
</footer>