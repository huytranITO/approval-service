package com.msb.bpm.approval.appr.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msb.bpm.approval.appr.model.request.cas.PostCASRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_INTERGATED_SEARCH;

@Validated
@RequestMapping("/v1/intergated")
public interface IntegratedApi {

  @GetMapping("/oprisk/person/{maDinhDanh}")
  @PreAuthorize(MANAGE_INTERGATED_SEARCH)
  ResponseEntity<ApiResponse> syncOpriskPerson(@PathVariable String maDinhDanh);

  @GetMapping("/oprisk/legal/{mst}")
  @PreAuthorize(MANAGE_INTERGATED_SEARCH)
  ResponseEntity<ApiResponse> syncOpriskLegal(@PathVariable String mst) throws JsonProcessingException;

  @GetMapping("/css/legal")
  ResponseEntity<ApiResponse> css(@RequestParam String profileId,
      @RequestParam String legalDocNo);

  @GetMapping("/cic/search-code")
  ResponseEntity<ApiResponse> searchCodeCIC(@RequestParam String customerUniqueId,
      @RequestParam String customerType);

  @GetMapping("/cic/kafka")
  ResponseEntity<ApiResponse> publishMessage(@RequestParam String clientQuestionId,
      @RequestParam String applicationBpmId);

  @PostMapping("/cas/info")
  ResponseEntity<ApiResponse> casInfo(@RequestBody @Valid PostCASRequest postCASRequest);

  @PostMapping("/cas/detail")
  ResponseEntity<ApiResponse> casDetail(@RequestBody @Valid PostCASRequest postCASRequest);

}
