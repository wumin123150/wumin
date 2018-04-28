package com.wumin.core.web.api;

import com.wumin.common.collection.MapUtil;
import com.wumin.common.dto.PageQuery;
import com.wumin.common.dto.ResponseResult;
import com.wumin.common.mapper.BeanMapper;
import com.wumin.core.dto.MessageDto;
import com.wumin.core.entity.Message;
import com.wumin.core.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(description = "我的消息接口")
@RestController
@RequestMapping(value = "/api/message")
public class MessageEndpoint {

  @Autowired
  private MessageService messageService;

  static {
    Map<String, String> mapFields = MapUtil.newHashMap();
    mapFields.put("text.content", "content");
    mapFields.put("text.sender", "sender");
    mapFields.put("text.createTime", "createTime");
    BeanMapper.registerClassMap(Message.class, MessageDto.class, mapFields);
  }

  @ApiOperation(value = "我的消息分页列表", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "myPage", method = RequestMethod.GET)
  public ResponseResult<Page<MessageDto>> myPage(PageQuery pageQuery, Integer readStatus, HttpServletRequest request) {
    Page<Message> page = messageService.getMyPage(pageQuery.getSearchKey(), readStatus, pageQuery.buildPageRequest());
    return ResponseResult.createSuccess(page, Message.class, MessageDto.class);
  }

  @ApiOperation(value = "我的消息标记已读", httpMethod = "GET", produces = "application/json")
  @RequestMapping(value = "read/{id}")
  public ResponseResult read(@PathVariable("id")String id, HttpServletRequest request) {
    messageService.read(id);
    return ResponseResult.SUCCEED;
  }

}
