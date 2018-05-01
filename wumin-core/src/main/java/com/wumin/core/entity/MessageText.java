package com.wumin.core.entity;

import com.wumin.common.entity.CreateRecord;
import com.wumin.common.entity.UuidEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * 消息内容
 *
 * @author walker
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "Core_Message_Text")
@SuppressWarnings("serial")
public class MessageText extends UuidEntity implements CreateRecord {

  private String content;//内容
  @ManyToOne
  @JoinColumn(name="sender")
  private BaseUser sender;//发送人
  private Date createTime;//创建时间
  private Long creator;//创建人

  public MessageText(String content, BaseUser sender) {
    this.content = content;
    this.sender = sender;
  }

  public boolean isCreated() {
    return StringUtils.isNotEmpty(id);
  }

}
