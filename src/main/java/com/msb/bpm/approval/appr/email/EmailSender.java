package com.msb.bpm.approval.appr.email;

import com.msb.bpm.approval.appr.client.email.EmailClient;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Component
@Slf4j
public class EmailSender {
  private final EmailClient emailClient;

  public EmailSender(EmailClient emailClient) {
    this.emailClient = emailClient;
  }

  public <T> void sendEmail(T message, String eventCode, String token) {
    if (message instanceof EmailRequest) {
      emailClient.sendEmail((EmailRequest) message, token);
      log.info("Sent email to eventCode {} successful", eventCode);
      log.info("Email data : {}", message);
    }
  }

  public <T> void sendInternalEmail(T message, String eventCode) {
    log.info("+++ Start send internal email");
    if (message instanceof EmailRequest) {
      emailClient.sendInternalEmail((EmailRequest) message);
      log.info("Sent Internal email to eventCode {} successful", eventCode);
      log.info("Email data : {}", message);
    }
  }

}
