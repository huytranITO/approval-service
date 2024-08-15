package com.msb.bpm.approval.appr.service.application;

import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.cbt.SearchByCustomerRelations;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryApplicationRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApplicationService {

  Object postCreateApplication(PostCreateApplicationRequest request);

  Object getApplication(String bpmId);
  Object searchIdAndVersionList(String bpmId);

  Object postSaveData(String bpmId, PostBaseRequest request);

  Object postQueryApplicationByCustomer(PostQueryApplicationRequest request);

  Object queryCICInfo(String bpmId, PostQueryCICRequest request);

  void postApplicationHandlingOfficer(PostAssignRequest request);

  void postApplicationCoordinator(PostAssignRequest request);

  void postReworkApplication(String bpmId, PostReworkApplicationRequest request);

  ApplicationEntity postSubmitApplication(String bpmId, PostBaseRequest request);

  void postCoordinatorAssignToTeamLead(String bpmId);

  void postCloseApplication(String bpmId, PostCloseApplicationRequest request);

  Object syncAmlOprInfo(String bpmId, PostSyncAmlOprRequest request);

  Object syncCreditRatingCSS(String bpmId, PostQueryCreditRatingRequest request);

  Object syncCreditRatingCSSV2(String bpmId, PostQueryCreditRatingRequest request);

  List<AuthorityDetailDTO> getAuthoritiesByApplication(String bpmId);

  Object getGenerateAndPreviewForms(String bpmId);

  void updateGeneratorStatus(String bpmId, String generatorStatus);

  Object verifySubmitApplication(String bpmId);

  Object getAccountInfo(String cifNumber);

  Object copyApplication(String bpmId);

  Object getCicDetail(String bpmId);

  Object searchByRelations(SearchByCustomerRelations request);

  Object syncOpRisk(OpRiskRequest opRiskRequest);

  Object getOpRiskDetail(String bpmId);

  Object findBranch();

  Object getApplicationPushKafka(String bpmId);

  ResponseEntity<ApiResponse> pushKafkaApplication(String bpmId);
}
