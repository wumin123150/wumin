package com.wumin.core.web;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

//@Component
public class Producer {

  @Autowired
  private JmsMessagingTemplate jmsTemplate;

  // 发送消息
  public void sendMessage(final String message){
    Destination destination = new ActiveMQQueue("wumin.queue");
    jmsTemplate.convertAndSend(destination, message);
  }

}
