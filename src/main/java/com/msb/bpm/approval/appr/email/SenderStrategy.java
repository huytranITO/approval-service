package com.msb.bpm.approval.appr.email;

import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
public interface SenderStrategy {
  void publishEmail(String eventCode);

  EventCodeEmailType getType();

  void setMetadata(ConcurrentMap<String, Object> metadata);
}
