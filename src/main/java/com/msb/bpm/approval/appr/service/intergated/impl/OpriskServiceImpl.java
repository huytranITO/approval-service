package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.NOT_ON_THE_LIST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ON_THE_LIST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.OPR_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.QUERY_ERROR_CODE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.RESULT_QUERY_ERROR;

import com.msb.bpm.approval.appr.client.oprisk.OpriskClient;
import com.msb.bpm.approval.appr.config.OpriskPersonConfig;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.AuthenInfo;
import com.msb.bpm.approval.appr.model.request.CheckBlackListPLegalRequest;
import com.msb.bpm.approval.appr.model.request.CheckBlackListPPersonRequest;
import com.msb.bpm.approval.appr.model.request.DataCheckBlackListPLegalRequest;
import com.msb.bpm.approval.appr.model.request.DataCheckBlackListPPersonRequest;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.response.cic.AmlOprDataResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.oprisklegal.SyncOpriskLegalResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson.SyncOpriskPersonResponse;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.intergated.OpriskService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpriskServiceImpl implements OpriskService {

  private final OpriskClient opriskClient;
  private final OpriskPersonConfig opriskPersonConfig;
  private final CommonService commonService;

  @Override
  public SyncOpriskPersonResponse syncOpriskPerson(String maDinhDanh) {

    CheckBlackListPPersonRequest checkBlackListPPersonRequest = new CheckBlackListPPersonRequest();
    AuthenInfo authenInfo = new AuthenInfo();
    authenInfo.setReqTime(new Date(System.currentTimeMillis()));
    authenInfo.setAuthorizer(opriskPersonConfig.getAuthorizer());
    authenInfo.setPassword(opriskPersonConfig.getPassword());
    authenInfo.setSrv(opriskPersonConfig.getSrvperson());
    authenInfo.setReqId(UUID.randomUUID().toString());
    authenInfo.setReqApp(opriskPersonConfig.getRegApp());
    DataCheckBlackListPPersonRequest dataCheckBlackListPPersonRequest =
        new DataCheckBlackListPPersonRequest();
    dataCheckBlackListPPersonRequest.setAuthenInfo(authenInfo);
    dataCheckBlackListPPersonRequest.setIdentityCard(maDinhDanh);
    checkBlackListPPersonRequest.setCheckBlackListP(dataCheckBlackListPPersonRequest);

    return opriskClient.syncOpriskPerson(checkBlackListPPersonRequest);
  }

  @Override
  public SyncOpriskLegalResponse syncOpriskLegal(String mst) {

    CheckBlackListPLegalRequest checkBlackListPLegalRequest = new CheckBlackListPLegalRequest();
    AuthenInfo authenInfo = new AuthenInfo();
    authenInfo.setReqTime(new Date(System.currentTimeMillis()));
    authenInfo.setAuthorizer(opriskPersonConfig.getAuthorizer());
    authenInfo.setPassword(opriskPersonConfig.getPassword());
    authenInfo.setSrv(opriskPersonConfig.getSrvlegal());
    authenInfo.setReqId(UUID.randomUUID().toString());
    authenInfo.setReqApp(opriskPersonConfig.getRegApp());
    DataCheckBlackListPLegalRequest dataCheckBlackListPLegalRequest =
        new DataCheckBlackListPLegalRequest();
    dataCheckBlackListPLegalRequest.setAuthenInfo(authenInfo);
    dataCheckBlackListPLegalRequest.setBusinessRegistrationNumber(mst);
    checkBlackListPLegalRequest.setCheckBlackListO(dataCheckBlackListPLegalRequest);

    return opriskClient.syncOpriskLegal(checkBlackListPLegalRequest);
  }

  @Override
  public void syncOprInfo(ApplicationEntity entityApp, PostSyncAmlOprRequest request) {
    log.info("syncOprInfo START with entityApp={}, request={}", entityApp, request);
    //Query info from AML
    List<AmlOprDataResponse> lstOprResult = new ArrayList<>();
    for (PostSyncAmlOprRequest.QueryAmlOpr item : request.getData()) {
      if (StringUtils.isBlank(item.getIdentifierCode())) {
        continue;
      }
      String customerType = item.getCustomerType().getTypeNumber();
      String subject = commonService.getCustomerType(item.getCustomerId(), customerType,
          entityApp.getCustomer().getId());

      AmlOprDataResponse response = AmlOprDataResponse.builder()
          .customerId(item.getCustomerId())
          .identifierCode(item.getIdentifierCode())
          .queryType(OPR_TAG)
          .priority(item.isPriority())
          .refCustomerId(item.getRefCustomerId())
          .subject(subject)
          .build();
      try {
        if (CustomerType.RB.getTypeNumber().equals(customerType)) {
          SyncOpriskPersonResponse data = syncOpriskPerson(item.getIdentifierCode());
          response.setResultCode(
              data.getCheckBlackListP().getRespMessage().getRespCode().toString());
          response.setResultDescription(
              getResultDesc(data.getCheckBlackListP().getRespMessage().getRespCode(),
                  data.getCheckBlackListP().getRespDomain().getBlackList4LosP().getDtcn()
              , data.getCheckBlackListP().getRespDomain().getBlackList4LosP().getResult()));
          response.setReasonInList(
              data.getCheckBlackListP().getRespDomain().getBlackList4LosP().getDscn());
          response.setStartDate(
              data.getCheckBlackListP().getRespDomain().getBlackList4LosP().getDateInput());
          response.setEndDate(
              data.getCheckBlackListP().getRespDomain().getBlackList4LosP().getDateEx());
        }

        if (CustomerType.EB.getTypeNumber().equals(customerType)) {
          SyncOpriskLegalResponse data = syncOpriskLegal(item.getIdentifierCode());
          response.setResultCode(
              data.getCheckBlackListO().getRespMessage().getRespCode().toString());
          response.setResultDescription(
              getResultDesc(data.getCheckBlackListO().getRespMessage().getRespCode(),
                  data.getCheckBlackListO().getRespDomain().getBlackList4LosO()
                      .getClassificationOfObjects()
              , data.getCheckBlackListO().getRespDomain().getBlackList4LosO().getResult()));
          response.setReasonInList(
              data.getCheckBlackListO().getRespDomain().getBlackList4LosO().getListedReasons());
          response.setStartDate(
              data.getCheckBlackListO().getRespDomain().getBlackList4LosO().getDateAdded());
          response.setEndDate(
              data.getCheckBlackListO().getRespDomain().getBlackList4LosO().getEndDate());
        }
      } catch (Exception ex) {
        log.error("[Sync OpRisk] with bpmId={}, maDinhDanh={} Error:", entityApp.getBpmId(),
            item.getIdentifierCode(), ex);
        response.setResultCode(QUERY_ERROR_CODE);
        response.setResultDescription(RESULT_QUERY_ERROR);
      }
      lstOprResult.add(response);
    }

    // Save to DB
    commonService.saveAmlOprInfo(entityApp, lstOprResult);
    log.info("syncOprInfo END with entityApp={}, request={}", entityApp, request);
  }

  private String getResultDesc(Long respCode, String valueRespCode, Boolean result) {
    String resultDescription;

    if (0L != respCode) {
      resultDescription = RESULT_QUERY_ERROR;
    } else if (Objects.equals(Boolean.TRUE, result)) {
      resultDescription = valueRespCode;
    } else {
      resultDescription = NOT_ON_THE_LIST;
    }

    return resultDescription;
  }
}
