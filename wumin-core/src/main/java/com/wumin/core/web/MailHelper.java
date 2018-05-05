package com.wumin.core.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

@Component
public class MailHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(MailHelper.class);

  @Value("${spring.mail.username}")
  private String from;

  @Autowired
  private JavaMailSender mailSender;
  @Autowired
  private TemplateEngine templateEngine;

  /**
   * 发送纯文本邮件.
   */
  public void sendMail(String to, String subject, String content) {
    sendMail(to, null, null, subject, content);
  }

  /**
   * 发送纯文本邮件.
   */
  public void sendMail(String to, String cc, String subject, String content) {
    sendMail(to, cc, null, subject, content);
  }

  /**
   * 发送纯文本邮件.
   */
  public void sendMail(String to, String cc, String bcc, String subject, String content) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    if (StringUtils.isNotEmpty(cc))
      msg.setCc(cc);
    if (StringUtils.isNotEmpty(bcc))
      msg.setBcc(bcc);
    msg.setSubject(subject);
    msg.setText(content);

    try {
      mailSender.send(msg);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("纯文本邮件已发送至{}", StringUtils.join(msg.getTo(), ","));
      }
    } catch (Exception e) {
      LOGGER.error("发送邮件失败", e);
    }
  }

  /**
   * 发送MIME格式邮件.
   */
  public void sendMail(String to, String subject, String templateName, Map<String, Object> model) {
    sendMail(to, subject, templateName, model, null);
  }

  /**
   * 发送MIME格式邮件.
   */
  public void sendMail(String to, String subject, String templateName, Map<String, Object> model, List<MultipartFile> attachments) {
    sendMail(to, null, null, subject, templateName, model, attachments);
  }

  /**
   * 发送MIME格式邮件.
   */
  public void sendMail(String to, String cc, String bcc, String subject, String templateName, Map<String, Object> model, List<MultipartFile> attachments) {
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

      String content = generateContent(templateName, model);

      helper.setFrom(from);
      helper.setTo(to);
      if (StringUtils.isNotEmpty(cc))
        helper.setCc(cc);
      if (StringUtils.isNotEmpty(bcc))
        helper.setBcc(bcc);
      helper.setSubject(subject);
      helper.setText(content, true);

      if (attachments != null) {
        for (MultipartFile attachment : attachments) {
          helper.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));
        }
      }

      mailSender.send(msg);
      LOGGER.info("HTML版邮件已发送至", to);
    } catch (MessagingException e) {
      LOGGER.error("构造邮件失败", e);
    } catch (Exception e) {
      LOGGER.error("发送邮件失败", e);
    }
  }

  /**
   * 使用Freemarker生成html格式内容.
   */
  private String generateContent(String templateName, Map<String, Object> model) {
      Context context = new Context();
      context.setVariables(model);
      return templateEngine.process(templateName, context);
  }

}
