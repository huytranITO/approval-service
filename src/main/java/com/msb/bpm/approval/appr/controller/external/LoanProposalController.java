package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.LoanProposalApi;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.loanproposal.LoanProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/11/2023, Monday
 **/

@RestController
@RequiredArgsConstructor
public class LoanProposalController implements LoanProposalApi {

  private final LoanProposalService loanProposalService;
  private final ApiRespFactory factory;

  @Override
  public ResponseEntity<ApiResponse> updateApplicationData(String bpmId) {
    loanProposalService.updateApplicationData(bpmId);
    return factory.success();
  }
}
