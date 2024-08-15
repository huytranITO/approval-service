package com.msb.bpm.approval.appr.notication;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_NOTIFICATION;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.BIZ_TYPE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CAS_SUCCESS_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CAS_SUCCESS_TITLE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_NEW;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.PD_RB;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.SYSTEM;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.USER;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.contents;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.titles;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.kafka.sender.GeneralKafkaSender;
import com.msb.bpm.approval.appr.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/*
* @author: BaoNV2
* @since: 16/10/2023 10:37 AM
* @description:
* @update:
*
* */
@Component
@Slf4j
@RequiredArgsConstructor
public class CASNotificationStrategy implements NotificationInterface {
  private final GeneralKafkaSender kafkaSender;

  @Value("${web.domain}")
  private String domain;

  private final ApplicationConfig applicationConfig;
  @Override
  public void notice(Object... params) {
    String bpmId = (String) params[0];
    String assignee = (String) params[1];
    String ratings = (String) params[2];
    log.info("START CASNotificationHandlerComponent with params: {}, {}", bpmId, assignee, ratings);

    List<String> mailTo = new ArrayList<>();
    mailTo.add(assignee);

    NoticeMessage msg = NoticeMessage.builder()
        .notificationType(BIZ_TYPE)
        .bizType(PD_NEW)
        .module(PD_RB)
        .from(SYSTEM)
        .recipientType(USER)
        .to(mailTo)
        .title(String.format(titles.get(CAS_SUCCESS_TITLE), bpmId))
        .content(String.format(contents.get(CAS_SUCCESS_CONTENT), ratings))
        .link(String.format("rb-common-approval/rm-request-detail/%s?currentTabIndex=3", StringUtil.getValue(bpmId)))
        .build();

    kafkaSender.sendMessage(msg, applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName());
    log.info("CASNotificationHandlerComponent END with topic: {}, message: {}",
            applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName(),
            msg);
  }

  @Override
  public void alert(Object... input) {
    //Nothing
  }

  public static String removeLastChar(String s) {
    return (s == null || s.length() == 0)
        ? null
        : (s.substring(0, s.length() - 1));
  }
}
