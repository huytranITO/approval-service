package com.msb.bpm.approval.appr.notication;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_NOTIFICATION;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.BIZ_TYPE;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CIC;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CIC_PDF_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CIC_PDF_TITLE;
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
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CICPDFNotificationStrategy implements NotificationInterface {
  private final GeneralKafkaSender kafkaSender;

  private final ApplicationConfig applicationConfig;

  @Override
  public void notice(Object... params) {
    String bpmId = (String) params[0];
    String assignee = (String) params[1];
    log.info("START CICPDFNotificationStrategy with params: bpmId {}, assignee {}", bpmId, assignee);

    List<String> mailTo = new ArrayList<>();
    mailTo.add(assignee);

    NoticeMessage msg = NoticeMessage.builder()
        .notificationType(BIZ_TYPE)
        .bizType(CIC)
        .module(PD_RB)
        .from(SYSTEM)
        .recipientType(USER)
        .to(mailTo)
        .title(String.format(titles.get(CIC_PDF_TITLE), bpmId))
        .content(contents.get(CIC_PDF_CONTENT))
        .link(String.format("rb-common-approval/rm-request-detail/%s?currentTabIndex=3", StringUtil.getValue(bpmId)))
        .build();

    kafkaSender.sendMessage(msg, applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName());
    log.info("CICPDFNotificationStrategy END with topic: {}, message: {}",
            applicationConfig.getKafka().getTopic().get(BPM_NOTIFICATION).getTopicName(),
            msg);
  }

  @Override
  public void alert(Object... input) {
    //Nothing
  }

}
