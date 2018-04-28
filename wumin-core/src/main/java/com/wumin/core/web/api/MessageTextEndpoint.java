package com.wumin.core.web.api;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.persistence.search.SearchRequest;
import com.wumin.core.dto.MessageTextDto;
import com.wumin.core.entity.Message;
import com.wumin.core.entity.MessageText;
import com.wumin.core.service.MessageService;
import com.wumin.core.service.MessageTextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "消息内容接口")
@RestController
@RequestMapping(value = "/api/messageText")
public class MessageTextEndpoint {

  @Autowired
  private MessageService messageService;
  @Autowired
  private MessageTextService messageTextService;

  @ApiOperation(value = "消息内容分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page", method = RequestMethod.GET)
  public ResponseResult<Page<MessageTextDto>> page(PageQuery pageQuery, HttpServletRequest request) {
    Page<MessageText> page = messageTextService.getPage(new SearchRequest(pageQuery, "content"));
    return ResponseResult.createSuccess(page, MessageText.class, MessageTextDto.class);
  }

  @ApiOperation(value = "消息内容简单分页列表", httpMethod = "GET", produces = "application/json")
  @RequiresRoles("admin")
  @RequestMapping(value = "page2", method = RequestMethod.GET)
  public ResponseResult<List<MessageTextDto>> page2(PageQuery pageQuery) {
    Page<MessageText> page = messageTextService.getPage(new SearchRequest(pageQuery, "content"));
    return ResponseResult.createSuccess(page.getContent(), page.getTotalElements(), MessageText.class, MessageTextDto.class);
  }

  @ApiOperation(value = "我的消息内容分页列表", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "myPage", method = RequestMethod.GET)
  public ResponseResult<Page<MessageTextDto>> myPage(PageQuery pageQuery, HttpServletRequest request) {
    Page<MessageText> page = messageTextService.getMyPage(pageQuery.getSearchKey(), pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page, MessageText.class, MessageTextDto.class);
  }

  @ApiOperation(value = "消息新增", httpMethod = "POST")
  @RequiresRoles("admin")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "content", value = "内容", required = true, dataType = "string", paramType = "form"),
    @ApiImplicitParam(name = "users", value = "接收人数组", required = true, dataType = "list", paramType = "form")
  })
  @RequestMapping(value = "create", method = RequestMethod.POST)
  public ResponseResult create(String content, Long[] users) {
    if(StringUtils.isEmpty(content)) {
      return ResponseResult.createParamError("内容不能为空");
    }
    if(users == null || users.length == 0) {
      return ResponseResult.createParamError("接收人不能为空");
    }

    List<Message> messages = messageService.save(content, null, ListUtil.newArrayList(users));
    return ResponseResult.SUCCEED;
  }

  @ApiOperation(value = "消息内容删除", httpMethod = "GET")
  @RequiresRoles("admin")
  @RequestMapping(value = "delete/{id}")
  public ResponseResult delete(@PathVariable("id") String id) {
    List<Message> messages = messageService.findByText(id);
    messageService.deleteInBatch(messages);
    messageTextService.delete(id);
    return ResponseResult.SUCCEED;
  }

}
