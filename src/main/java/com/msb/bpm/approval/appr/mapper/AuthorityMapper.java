package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDataDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityClientRequest;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityDataResponse;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityDetailResponse;
import com.msb.bpm.approval.appr.model.response.authority.AuthorityInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorityMapper {

    AuthorityMapper INSTANCE = Mappers.getMapper(AuthorityMapper.class);


    @Mapping(target = "authorities", source = "data")
    AuthorityDataDTO toAuthorityDto(AuthorityDataResponse data);


    AuthorityDetailDTO toAuthorityInfo(AuthorityInfoResponse data);

    @Mapping(target = "code", source = "info.code")
    @Mapping(target = "name", source = "info.name")
    @Mapping(target = "title", source = "info.title")
    @Mapping(target = "level", source = "info.level")
    @Mapping(target = "rank", source = "info.rank")
    @Mapping(target = "priority", source = "info.priority")
    @Mapping(target = "version", source = "version")
    AuthorityDetailDTO toAuthorityRuleInfo(AuthorityDetailResponse data);

    @Mapping(target = "customerType", source = "request.customerType")
    @Mapping(target = "submissionPurpose", source = "request.submissionPurpose")
    @Mapping(target = "customerGroup", source = "request.customerGroup")
    @Mapping(target = "creditRiskType", source = "request.creditRiskType")
    @Mapping(target = "approvalFlow", source = "request.approvalFlow")
    @Mapping(target = "segmentCustomer", source = "request.segmentCustomer")
    @Mapping(target = "riskGroup", source = "request.riskGroup")
    @Mapping(target = "authorization", source = "request.authorization")
    @Mapping(target = "applicationId", source = "request.applicationId")
    AuthorityClientRequest toRequestAuthorityClient(AuthorityCheckerRequest request);
}