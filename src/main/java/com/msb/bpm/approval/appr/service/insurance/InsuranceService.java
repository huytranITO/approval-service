package com.msb.bpm.approval.appr.service.insurance;

import com.msb.bpm.approval.appr.model.request.insurance.PostPublishInsuranceInfoRequest;

public interface InsuranceService {
  void execute(String bpmId, PostPublishInsuranceInfoRequest request);
}
