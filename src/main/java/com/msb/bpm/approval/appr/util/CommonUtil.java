package com.msb.bpm.approval.appr.util;

import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

  public static String getStatus(ApplicationEntity applicationEntity) {
    String status = applicationEntity.getStatus();
    if (applicationEntity.getLdpStatus() != null && !applicationEntity.getLdpStatus()
        .equals(ApplicationStatus.AS4000.getValue())) {
      status = applicationEntity.getLdpStatus();
    }
    return status;
  }
}
