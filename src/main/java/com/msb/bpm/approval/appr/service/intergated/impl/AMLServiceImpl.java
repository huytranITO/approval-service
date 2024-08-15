package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.AML_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.NOT_ON_THE_LIST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.QUERY_ERROR_CODE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.RESULT_QUERY_ERROR;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ON_THE_LIST;

import com.msb.bpm.approval.appr.client.aml.AMLClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest.QueryAmlOpr;
import com.msb.bpm.approval.appr.model.response.cic.AmlOprDataResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.aml.GetAMLInfoResponse;
import com.msb.bpm.approval.appr.service.intergated.AMLService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AMLServiceImpl implements AMLService {

  private final AMLClient amlClient;
  private final CommonService commonService;

  @Transactional
  @Override
  public void syncAmlInfo(ApplicationEntity entityApp, PostSyncAmlOprRequest request) {
    log.info("syncAmlInfo START with entityApp={}, request={}", entityApp, request);
    //Query info from AML
    List<AmlOprDataResponse> lstAmlResult = new ArrayList<>();
    for (QueryAmlOpr item : request.getData()) {
      if (StringUtils.isBlank(item.getIdentifierCode())) {
        continue;
      }
      GetAMLInfoResponse data;
      String subject = commonService.getCustomerType(item.getCustomerId(),
          item.getCustomerType().getTypeNumber(), entityApp.getCustomer().getId());
      AmlOprDataResponse response = AmlOprDataResponse.builder()
          .customerId(item.getCustomerId())
          .identifierCode(item.getIdentifierCode())
          .queryType(AML_TAG)
          .priority(item.isPriority())
          .refCustomerId(item.getRefCustomerId())
          .subject(subject)
          .build();
      try {
        data = amlClient.getAmlInfo(item.getIdentifierCode());

        response.setResultCode(
            data.getGetCheckAmlPublic().getRespMessage().getRespCode().toString());
        response.setResultDescription(
            500l != data.getGetCheckAmlPublic().getRespMessage().getRespCode() ?
                NOT_ON_THE_LIST : ON_THE_LIST);
      } catch (Exception ex) {
        log.error("[Sync AML] with bpmId={}, idPassport={} Error:", entityApp.getBpmId(),
            item.getIdentifierCode(), ex);
        if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
            ((ApprovalException)ex).getCode())) {
          throw new ApprovalException(DomainCode.AML_SERVICE_ERROR,
              ((ApprovalException)ex).getArgs());
        }
        response.setResultCode(QUERY_ERROR_CODE);
        response.setResultDescription(RESULT_QUERY_ERROR);
      }
      lstAmlResult.add(response);
    }

    // Save to DB
    commonService.saveAmlOprInfo(entityApp, lstAmlResult);
    log.info("syncAmlInfo END with entityApp={}, request={}", entityApp, request);
  }
}
