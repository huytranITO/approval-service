package com.msb.bpm.approval.appr.notication;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_NOTIFICATION;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.BIZ_TYPE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.LDP_SUCCESS_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.LDP_SUCCESS_TITLE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_NEW;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_RB;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.SYSTEM;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.USER;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.contents;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.titles;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.kafka.sender.GeneralKafkaSender;
import com.msb.bpm.approval.appr.util.Util;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
* @author: BaoNV2
* @since: 16/10/2023 12:46 PM
* @description:  
* @update:
*
* */
@Component
@Slf4j
@RequiredArgsConstructor
public class LandingPageNotificationStrategy implements NotificationInterface {
  private final GeneralKafkaSender kafkaSender;

  @Value("${web.domain}")
  private String domain;

  private final ApplicationConfig applicationConfig;

  @Override
  public void notice(Object... params) {
    log.info("START LandingPageNotificationStrategy send notification");

    try {
      String customerName = (String) params[0];
      String bpmId = (String) params[1];
      String assignee = (String) params[2];

      log.info("LandingPageNotificationStrategy start with params: {}, {}, {}", customerName, bpmId, assignee);

      List<String> mailTo = new ArrayList<>();
      mailTo.add(assignee);

      NoticeMessage msg = NoticeMessage.builder()
          .notificationType(BIZ_TYPE)
          .bizType(PD_NEW)
          .module(PD_RB)
          .from(SYSTEM)
          .recipientType(USER)
          .to(mailTo)
          .title(String.format(titles.get(LDP_SUCCESS_TITLE), bpmId))
          .content(String.format(contents.get(LDP_SUCCESS_CONTENT), customerName))
          .link(Util.escapeMetaCharacters(String.format("%s/v2/request-list-approval/request-need-processing?page=0&size=10&sorts=createdAt,desc", domain)))
          .build();

      kafkaSender.sendMessage(msg, applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName());
      log.info("LandingPageNotificationStrategy END with topic: {}, message: {}",
              applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName(),
              msg);
    } catch (Exception e) {
      log.error("notice error {}", e);
      e.printStackTrace();
    }
  }

  @Override
  public void alert(Object... input) {
  //Nothing
  }
}
