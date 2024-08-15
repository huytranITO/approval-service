package com.msb.bpm.approval.appr.service.notification;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import java.util.Set;

public interface NotificationService {

  void noticeRatioCIC(Set<String> users, String applicationBpmId);

  void noticeRatioCIC(String user, String applicationBpmId);

  void noticePdfCic(String currentUser, ApplicationEntity application);

  void noticePdfCIC(Set<String> users, String applicationBpmId);

  void noticePdfCIC(String user, String applicationBpmId);

  void noticeUpdateRatioCIC(Set<String> users, String applicationBpmId);
}
