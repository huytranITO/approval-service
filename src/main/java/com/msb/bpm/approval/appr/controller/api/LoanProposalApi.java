package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/11/2023, Monday
 **/
@RequestMapping("/loan-proposal/rb")
public interface LoanProposalApi {

  @PostMapping("/api/v1/application/{bpmId}/update-application-data")
  ResponseEntity<ApiResponse> updateApplicationData(@PathVariable String bpmId);
}
