package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthoritySearchRequest;
import com.msb.bpm.approval.appr.model.request.authority.UserAuthorityRequest;
import java.util.List;

public interface AuthorityService {

    Object searchAuthority(UserAuthorityRequest request, String token);

    Object searchAuthoirty(AuthoritySearchRequest request, String token);

    Object checkAuthority(AuthorityCheckerRequest request, String token, Integer ruleVersion);

    List<AuthorityDetailDTO> filterAuthority(String type, String code, String customerType, String active);
}