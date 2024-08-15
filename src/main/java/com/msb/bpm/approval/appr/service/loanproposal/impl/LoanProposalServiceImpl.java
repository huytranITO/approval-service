package com.msb.bpm.approval.appr.service.loanproposal.impl;

import com.msb.bpm.approval.appr.client.loanproposal.LoanProposalClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.model.dto.feedback.ApplicationFbDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.application.impl.ApplicationFeedbackServiceImpl;
import com.msb.bpm.approval.appr.service.loanproposal.LoanProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/11/2023, Monday
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class LoanProposalServiceImpl implements LoanProposalService {

  private final LoanProposalClient loanProposalClient;
  private final ApplicationFeedbackServiceImpl applicationFeedbackService;
  private final ApplicationRepository applicationRepository;

  @Override
  @Transactional
  public void updateApplicationData(String bpmId) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    log.info("LoanProposalServiceImpl.updateApplicationData execute with bpmId : {}", bpmId);

    ApplicationFbDTO applicationFbDTO = applicationFeedbackService.convertApplicationEntityToApplicationFbDTO(applicationEntity);

    loanProposalClient.replaceDataApplication(ApplicationMapper.INSTANCE.convertFeedbackToCmsV2Request(applicationFbDTO));
  }
}
