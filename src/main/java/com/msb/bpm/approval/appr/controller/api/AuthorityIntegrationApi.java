package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthoritySearchRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api/v1/authority")
public interface AuthorityIntegrationApi {

    @PostMapping("/search")
    ResponseEntity<ApiResponse> searchAuthority(@RequestBody AuthoritySearchRequest request);

    @PostMapping("/check")
    ResponseEntity<ApiResponse> checkAuthority(@RequestBody @Valid AuthorityCheckerRequest request);

    @PostMapping("/user/search")
    ResponseEntity<ApiResponse> searchUserAuthority(@RequestBody UserAuthorityRequest request);

}