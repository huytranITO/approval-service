package com.msb.bpm.approval.appr.service.intergated.impl;

import com.msb.bpm.approval.appr.client.authority.AuthorityClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.AuthorityMapper;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDataDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityClientRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthoritySearchRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityCheckResponse;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityDataResponse;
import com.msb.bpm.approval.appr.model.response.authority.UserAuthorityResponse;
import com.msb.bpm.approval.appr.service.intergated.AuthorityService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityClient client;

    private final AuthorityMapper authorityMapper;

    @Override
    public UserAuthorityResponse searchAuthority(UserAuthorityRequest request, String token) {
        UserAuthorityResponse response = null;
        try {
            response = client.searchAuthority(request, token);
        } catch (Exception e) {
            throw new ApprovalException(DomainCode.AUTHORITY_SERVICE_ERROR);
        }
        return response;
    }

    @Override
    public Object searchAuthoirty(AuthoritySearchRequest request, String token) {
        AuthorityDataResponse authorityResponse = client.searchAuthority(request, token);
        if (authorityResponse == null) {
            throw new ApprovalException(DomainCode.AUTHORITY_SERVICE_ERROR);
        }
        return authorityMapper.toAuthorityDto(authorityResponse);
    }

    @Override
    public AuthorityDetailDTO checkAuthority(AuthorityCheckerRequest request, String token, Integer version) {

        AuthorityClientRequest requestClient = authorityMapper.toRequestAuthorityClient(request);

        AuthorityCheckResponse authorityDetailResponse = client.checkAuthority(
                requestClient.withVersion(version)
                        .withLoanLimitTotal(request.getLoanLimitTotal())
                        .withLoanLimitWithoutProductTotal(request.getLoanLimitWithoutProductTotal())
                        .withLoanTotalCollateral(request.getLoanTotalCollateral())
                        .withTotal(request.getTotal()), token);

        if (authorityDetailResponse == null) {
            throw new ApprovalException(DomainCode.AUTHORITY_SERVICE_ERROR);
        }
        if(authorityDetailResponse.getData() == null) {
            throw new ApprovalException(DomainCode.AUTHORITY_APPLICATION_ERROR);
        }

        return authorityMapper.toAuthorityRuleInfo(authorityDetailResponse.getData());
    }

    @Override
    public List<AuthorityDetailDTO> filterAuthority(String type, String code, String customerType, String active) {
        AuthorityDataDTO authorityDataDTO = (AuthorityDataDTO) searchAuthoirty(
                new AuthoritySearchRequest().withType(type).withCustomerType(customerType)
                        .withActive(active), HeaderUtil.getToken());

        List<AuthorityDetailDTO> authorities = authorityDataDTO.getAuthorities();

        if (StringUtils.isNotBlank(code)) {
            AuthorityDetailDTO currentUserAuthority = authorities.stream()
                .filter(authority -> authority.getCode().equalsIgnoreCase(code)).findFirst()
                .orElse(null);

            if (currentUserAuthority == null) {
                return Collections.emptyList();
            }

            return authorities.stream()
                .filter(
                    authority ->
                        currentUserAuthority.getRank().equalsIgnoreCase(authority.getRank())
                            && authority.getPriority().compareTo(currentUserAuthority.getPriority())
                            > -1)
                .collect(Collectors.toList());
        }

        return authorities;
    }

}