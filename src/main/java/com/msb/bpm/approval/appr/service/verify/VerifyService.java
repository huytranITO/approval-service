package com.msb.bpm.approval.appr.service.verify;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityRequestMapDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostCompleteRequest;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import java.util.Set;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
public interface VerifyService {

  void verifyApplicationData(String bpmId);

  void verifyLimitCredit(Set<ApplicationLimitCreditDTO> limitCredits, Set<ApplicationCreditDTO> credits);

  void verifyApplicationAuthority(AuthorityRequestMapDTO authorityRequestMapDTO);

  void verifyOTP(PostCompleteRequest request);

  void verifyUserReception(String userReception, RuleResponse transitionRule);

  void verifyDocumentsInfo(String bpmId, boolean isCheckGenerator);

  void verifyUserCanView(ApplicationEntity applicationEntity);

  void verifyCicWhenSubmit(ApplicationEntity applicationEntity);

  boolean compareWithApplicationLDP(ApplicationEntity applicationEntity);

  Object verifyLdpStatusWhenSubmit(String bpmId);

  void verifyAccountNumber(ApplicationEntity entity);

  void verifyRmCommitStatus(ApplicationEntity entity);

  void verifyOpRiskCollateral(ApplicationEntity applicationEntity);
  void verifyAssetInfoStatus(String bpmId);

  void verifyCIFIStatus(String bpmId);

  void verifyIncomeCreditRatingsCss(ApplicationEntity applicationEntity);
}
