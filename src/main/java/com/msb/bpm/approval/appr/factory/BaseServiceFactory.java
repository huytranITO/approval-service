package com.msb.bpm.approval.appr.factory;

import com.msb.bpm.approval.appr.service.BaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseServiceFactory {

  private final List<BaseService> baseServices;

  public BaseService getBaseRequest(String type) {
    for (BaseService baseService : baseServices) {
      if (type.equalsIgnoreCase(baseService.getType())) {
        return baseService;
      }
    }
    return null;
  }
}
