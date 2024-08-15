package com.msb.bpm.approval.appr.notication;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_NOTIFICATION;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.BIZ_TYPE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.FORM_TEMPLATE_ERROR_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.FORM_TEMPLATE_ERROR_TITLE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.FORM_TEMPLATE_SUCCESS_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.FORM_TEMPLATE_SUCCESS_TITLE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_NEW;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_RB;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.SYSTEM;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.USER;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.contents;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.titles;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.kafka.sender.GeneralKafkaSender;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * @author: BaoNV2
 * @since: 16/10/2023 11:18 AM
 * @description:
 * @update:
 *
 * */
@Component
@Slf4j
@AllArgsConstructor
public class FormTemplateNotificationStrategy implements NotificationInterface {

  private final GeneralKafkaSender kafkaSender;

  private final ApplicationConfig applicationConfig;
  @Override
  public void notice(Object... params) {
    try {
      String bpmId = (String) params[0];
      String assignee = (String) params[1];

      log.info("START FormTemplateNotificationStrategy with params : {}, {}", bpmId, assignee);

      List<String> mailTo = new ArrayList<>();
      mailTo.add(assignee);

      NoticeMessage msg = NoticeMessage.builder()
          .notificationType(BIZ_TYPE)
          .bizType(PD_NEW)
          .module(PD_RB)
          .from(SYSTEM)
          .recipientType(USER)
          .to(mailTo)
          .title(String.format(titles.get(FORM_TEMPLATE_SUCCESS_TITLE), bpmId))
          .content(String.format(contents.get(FORM_TEMPLATE_SUCCESS_CONTENT), bpmId))
          .build();

      kafkaSender.sendMessage(msg, applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName());
      log.info("FormTemplateNotificationStrategy END with topic: {}, message: {}",
              applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName(),
          msg);

    } catch (Exception e) {
      log.info("FormTemplateNotificationStrategy Exception: {}", e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void alert(Object... params) {
    try {
      String bpmId = (String) params[0];
      String assignee = (String) params[1];
      log.info("START FormTemplateNotificationStrategy with: {}, {}", bpmId, assignee);

      List<String> mailTo = new ArrayList<>();
      mailTo.add(assignee);

      NoticeMessage msg = NoticeMessage.builder()
          .notificationType(BIZ_TYPE)
          .bizType(PD_NEW)
          .module(PD_RB)
          .from(SYSTEM)
          .recipientType(USER)
          .to(mailTo)
          .title(String.format(titles.get(FORM_TEMPLATE_ERROR_TITLE), bpmId))
          .content(String.format(contents.get(FORM_TEMPLATE_ERROR_CONTENT), bpmId))
          .build();

      kafkaSender.sendMessage(msg, applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName());
      log.info("FormTemplateNotificationStrategy END with topic: {}, message: {}",
              applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName(),
              msg);
    } catch (Exception e) {
      log.error("alert error {}", e);
      e.printStackTrace();
    }
  }
}
