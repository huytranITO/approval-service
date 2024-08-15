package com.msb.bpm.approval.appr.service.notification;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.notication.CICPDFNotificationStrategy;
import com.msb.bpm.approval.appr.notication.CICRatioNotificationStrategy;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final CICRatioNotificationStrategy cicRatioNotificationStrategy;
  private final CICPDFNotificationStrategy cicpdfNotificationStrategy;

  @Override
  public void noticeRatioCIC(Set<String> users, String applicationBpmId) {
    if (CollectionUtils.isEmpty(users)
        || !StringUtils.hasText(applicationBpmId)) {
      return;
    }
    for (String user : users) {
      noticeRatioCIC(user, applicationBpmId);
    }
  }

  @Override
  public void noticeRatioCIC(String user, String applicationBpmId) {
    if (!StringUtils.hasText(user)
        || !StringUtils.hasText(applicationBpmId)) {
      return;
    }
    cicRatioNotificationStrategy.notice(applicationBpmId, user);
  }

  @Override
  public void noticePdfCic(String currentUser, ApplicationEntity application) {
    Set<String> notifiedUsers = new HashSet<>();
    if (currentUser != null) {
      notifiedUsers.add(currentUser);
    }
    if (application.getAssignee() != null) {
      notifiedUsers.add(application.getAssignee());
    }
    noticePdfCIC(notifiedUsers, application.getBpmId());
  }

  @Override
  public void noticePdfCIC(Set<String> users, String applicationBpmId) {
    if (CollectionUtils.isEmpty(users)
        || !StringUtils.hasText(applicationBpmId)) {
      return;
    }
    for (String user : users) {
      noticePdfCIC(user, applicationBpmId);
    }
  }

  @Override
  public void noticePdfCIC(String user, String applicationBpmId) {
    if (!StringUtils.hasText(user)
        || !StringUtils.hasText(applicationBpmId)) {
      return;
    }
    cicpdfNotificationStrategy.notice(applicationBpmId, user);
  }

  @Override
  public void noticeUpdateRatioCIC(Set<String> users, String applicationBpmId) {
    if (CollectionUtils.isEmpty(users)
            || !StringUtils.hasText(applicationBpmId)) {
      return;
    }
    for (String user : users) {
      if (!StringUtils.hasText(user)
              || !StringUtils.hasText(applicationBpmId)) {
        continue;
      }
      cicRatioNotificationStrategy.noticeUpdate(applicationBpmId, user);
    }
  }
}
