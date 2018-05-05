package com.wumin.core.web;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//@Component
public class Consumer {

  @JmsListener(destination = "wumin.queue")
  public void receiveQueue(String text) {
    System.out.println(text);
  }

}
