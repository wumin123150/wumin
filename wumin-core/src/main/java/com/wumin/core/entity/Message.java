package com.wumin.core.entity;

import com.wumin.common.entity.UuidEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * 消息
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Message")
@SuppressWarnings("serial")
public class Message extends UuidEntity {

  public static final Integer SEND_STATUS_SENDING = 0;
  public static final Integer SEND_STATUS_SENT = 1;
  public static final Integer SEND_STATUS_FAIL = 2;

  public static final Integer READ_STATUS_UNREAD = 0;
  public static final Integer READ_STATUS_READED = 1;

  @ManyToOne
  @JoinColumn(name = "text_id")
  private MessageText text;//内容
  @ManyToOne
  @JoinColumn(name = "receiver")
  private BaseUser receiver;//接收人
  private Integer sendStatus;//发送状态(0:发送中,1:已发送,2:发送失败)
  private Date sendTime;//发送时间
  private Integer readStatus;//阅读状态(0:未读,1:已读)
  private Date readTime;//阅读时间

  public Message(MessageText text, BaseUser receiver) {
    this.text = text;
    this.receiver = receiver;
    this.sendStatus = SEND_STATUS_SENDING;
    this.readStatus = READ_STATUS_UNREAD;
  }

}
