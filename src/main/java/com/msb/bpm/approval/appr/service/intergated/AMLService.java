package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;

public interface AMLService {

  void syncAmlInfo(ApplicationEntity entityApp, PostSyncAmlOprRequest request);
}
