package com.msb.bpm.approval.appr.notication;

import static com.msb.bpm.approval.appr.exception.DomainCode.SEND_NOTIFICATION_ERROR;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/*
* @author: BaoNV2
* @since: 16/10/2023 11:24 AM
* @description:
* @update:
*
* */

@Component
public class NotificationFactory {
  @Autowired
  private CASNotificationStrategy casNotificationStrategy;
  @Autowired
  private LandingPageNotificationStrategy ldpNotificationStrategy;
  @Autowired
  private FormTemplateNotificationStrategy templateNotificationStrategy;

  public final NotificationInterface getNotification(NoticeType noticeType) {
    switch (noticeType) {
      case LDP: return ldpNotificationStrategy;
      case CAS: return casNotificationStrategy;
      case FORM_TEMPLATE: return templateNotificationStrategy;
      default:
        throw new ApprovalException(SEND_NOTIFICATION_ERROR);
    }
  }
}
