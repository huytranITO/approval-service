package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;

public interface BpmOperationService {

    void syncBpmOperation(ApplicationEntity entityApp, boolean isRetry);

    void updateStatusCredit(String creditInfo);

}