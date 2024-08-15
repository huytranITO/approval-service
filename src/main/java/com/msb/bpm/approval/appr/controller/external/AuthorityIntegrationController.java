package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.AuthorityIntegrationApi;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthoritySearchRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.AuthorityService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorityIntegrationController implements AuthorityIntegrationApi {

    private final ApiRespFactory apiRespFactory;
    private final AuthorityService authorityService;
    private final DecisionRuleIntegrateService decisionRuleIntegrateService;
    private final ApplicationRepository applicationRepository;

    @Override
    public ResponseEntity<ApiResponse> searchAuthority(AuthoritySearchRequest request) {
        return apiRespFactory.success(authorityService.searchAuthoirty(request, HeaderUtil.getToken()));
    }

    @Override
    public ResponseEntity<ApiResponse> checkAuthority(AuthorityCheckerRequest request) {
        ApplicationEntity applicationEntity = applicationRepository.findByBpmId(request.getApplicationId())
                .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
        return apiRespFactory.success(decisionRuleIntegrateService.getDecisionRule
                (applicationEntity.getId(), RuleCode.R002, request));
    }

    @Override
    public ResponseEntity<ApiResponse> searchUserAuthority(UserAuthorityRequest request) {
        return apiRespFactory.success(authorityService.searchAuthority(request, HeaderUtil.getToken()));
    }

}