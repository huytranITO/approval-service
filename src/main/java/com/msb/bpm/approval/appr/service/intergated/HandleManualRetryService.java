package com.msb.bpm.approval.appr.service.intergated;

import java.util.Set;

public interface HandleManualRetryService {

  public void processRetryWay4(Set<Long> request);

  public void processRetryOperation(Set<String> request);
}
