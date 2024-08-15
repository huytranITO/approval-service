package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.cic.AmlOprDataResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.springframework.transaction.annotation.Transactional;

public interface CommonService {
    @Transactional
    void saveAmlOprInfo(ApplicationEntity entityApp, List<AmlOprDataResponse> lstAmlOprData);

    @Transactional(readOnly = true)
    ApplicationEntity findAppByBpmId(String bpmId);

    @Transactional(readOnly = true)
    String getCustomerType(Long customerId, String customerType, Long cusId);

    @Transactional(readOnly = true)
    ApplicationEntity getAppEntity(ConcurrentMap<String, Object> metadata);

    @Transactional(readOnly = true)
    String genBpmId();

    @Transactional
    void saveDraft(String bpmId);

    GetUserProfileResponse getUserDetail(String username);

    String getAddressTypeValue(String customerType, String addressType);

    String getDistrictValue(String cityCode, String districtCode);

    String getWardValue(String districtCode, String wardCode);

    String convertRelationshipTypeSourceToBPM(String sourceApplication, String relationshipType);

    String convertRelationshipTypeBPMToSource(String sourceApplication, String relationshipType);
}
