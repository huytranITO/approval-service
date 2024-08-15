package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.request.cas.PostCASRequest;

public interface CASService {
  void getCASInfo(PostCASRequest postCASRequest);

  ApplicationCreditRatingsDTO getCASDetail(PostCASRequest postCASRequest);

}
