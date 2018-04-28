package com.wumin.core.web.controller;

import com.wumin.common.io.ImageUtil;
import com.wumin.common.web.FileRepository;
import com.wumin.core.entity.Annex;
import com.wumin.core.entity.User;
import com.wumin.core.service.AnnexService;
import com.wumin.core.service.UserService;
import com.wumin.core.utils.DownloadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 附件
 *
 * @author walker
 */
@Api(description = "附件管理")
@Controller
public class AnnexController {

  private static final String MENU = "annex";

  @Value("${system.theme:ace}")
  private String theme;

  @Autowired
  private AnnexService annexService;
  @Autowired
  private FileRepository fileRepository;
  @Autowired
  private UserService userService;

  @ApiOperation(value = "附件列表页面", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "/admin/annex")
  public String list(Model model) {
    model.addAttribute("menu", MENU);
    return theme + "/annex/annexList";
  }

  @ApiOperation(value = "附件下载", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "/admin/annex/download/{id}")
  public void download(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Annex attachment = annexService.get(id);
    File file = fileRepository.retrieve(attachment.getPath());

    if ((!file.exists() || file.length() == 0) && ImageUtil.isScale(file)) {
      String scaleRange = ImageUtil.getScaleRange(file);
      File srcFile = new File(ImageUtil.getScaleSource(file));
      ImageUtil.forceScale(srcFile, file, scaleRange);
    }

    DownloadUtil.download(file, attachment.getName(), request, response);
  }

  @ApiOperation(value = "附件下载", httpMethod = "GET")
  @RequestMapping(value = "/annex/{path1}/{name:.+}", method = RequestMethod.GET)
  public void file(@PathVariable String path1, @PathVariable String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String path = "/" + path1 + "/" + name;
    handle(path, request, response);
  }

  @ApiOperation(value = "附件下载", httpMethod = "GET")
  @RequestMapping(value = "/annex/{path1}/{path2}/{name:.+}", method = RequestMethod.GET)
  public void file(@PathVariable String path1, @PathVariable String path2, @PathVariable String name, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    String path = "/" + path1 + "/" + path2 + "/" + name;
    handle(path, request, response);
  }

  @ApiOperation(value = "用户头像下载", httpMethod = "GET")
  @RequestMapping(value = "/avatar/{id}", method = RequestMethod.GET)
  public void avatar(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    User user = userService.get(id);
    String path = user.getAvatar();
    handle(path, request, response);
  }

  private void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
    File file = fileRepository.retrieve(path);
    String fileName = FilenameUtils.getName(file.getName());

    Annex attachment = annexService.getByPath(path);
    if (attachment != null) {
      fileName = attachment.getName();
    }

    if ((!file.exists() || file.length() == 0) && ImageUtil.isScale(file)) {
      String scaleRange = ImageUtil.getScaleRange(file);
      File srcFile = new File(ImageUtil.getScaleSource(file));
      ImageUtil.forceScale(srcFile, file, scaleRange);
    }

    DownloadUtil.download(file, fileName, request, response);
  }

}
