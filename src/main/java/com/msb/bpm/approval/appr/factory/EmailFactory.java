package com.msb.bpm.approval.appr.factory;

import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 22/5/2023, Monday
 **/
@Component
@RequiredArgsConstructor
public class EmailFactory {

  private final List<SenderStrategy> senderStrategies;

  public SenderStrategy getSenderStrategy(EventCodeEmailType type) {
    for (SenderStrategy strategy : senderStrategies) {
      if (type.equals(strategy.getType())) {
        return strategy;
      }
    }
    return null;
  }
}
