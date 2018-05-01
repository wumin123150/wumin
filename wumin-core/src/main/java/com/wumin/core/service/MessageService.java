package com.wumin.core.service;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.common.web.ThreadContext;
import com.wumin.core.dao.MessageDao;
import com.wumin.core.entity.BaseUser;
import com.wumin.core.entity.Message;
import com.wumin.core.entity.MessageText;
import com.wumin.core.entity.QMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消息管理业务类.
 *
 * @author walker
 */
@Service
@Transactional
public class MessageService extends JpaServiceImpl<Message, String> {

//  @Autowired
//  private SimpMessagingTemplate simpMessagingTemplate;
  @Autowired
  private MessageTextService messageTextService;
  @Autowired
  private MessageDao messageDao;

  public List<Message> save(String content, Long sender, List<Long> receivers) {
    MessageText text = messageTextService.save(new MessageText(content, sender != null ? new BaseUser(sender) : null));

    List<Message> messages = ListUtil.newArrayList();
    for(Long receiver : receivers) {
      Message message = save(new Message(text, new BaseUser(receiver)));
      messages.add(message);
//      simpMessagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/message", message);
    }

    return messages;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void read(String id) {
    Message message = get(id);
    message.setReadStatus(Message.READ_STATUS_READED);
    message.setReadTime(new Date());
    save(message);
  }

  public List<Message> findByText(String textId) {
    return messageDao.findByText(textId);
  }

  public Page<Message> getMyPage(final String searchKey, final Integer readStatus, Pageable page) {
    QMessage qMessage = QMessage.message;
    List<BooleanExpression> expressions = new ArrayList<>();

    expressions.add(qMessage.receiver.id.eq(ThreadContext.getUserId()));

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qMessage.text.content.like(StringUtils.trimToEmpty(searchKey)));
    }

    if (readStatus != null) {
      expressions.add(qMessage.readStatus.eq(readStatus));
    }

    return messageDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public long countMyUnread(final String loginName) {
    QMessage qMessage = QMessage.message;
    List<BooleanExpression> expressions = new ArrayList<>();
    expressions.add(qMessage.receiver.loginName.eq(loginName));
    expressions.add(qMessage.readStatus.eq(Message.READ_STATUS_UNREAD));
    return messageDao.count(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])));
  }

}
