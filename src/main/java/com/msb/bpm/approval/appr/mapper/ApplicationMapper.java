package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.ApplicationFbDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationMapper {

  ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

  ApplicationDTO toDTO(ApplicationEntity application);

  @Mapping(target = "limitCredits", ignore = true)
  @Mapping(target = "creditRatings", ignore = true)
  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "repayment", ignore = true)
  @Mapping(target = "creditConditions", ignore = true)
  void referenceApplicationEntityByDebtInfoRequest(@MappingTarget ApplicationEntity e1,
      PostDebtInfoRequest e2);

  CmsApplicationDTO entityToCmsApplicationDto(ApplicationEntity applicationEntity);


  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "approvalType", source = "approvalType")
  @Mapping(target = "processFlow", source = "processFlow")
  @Mapping(target = "processFlowValue", source = "processFlowValue")
  @Mapping(target = "submissionPurpose", source = "submissionPurpose")
  @Mapping(target = "submissionPurposeValue", source = "submissionPurposeValue")
  @Mapping(target = "segment", source = "segment")
  @Mapping(target = "partnerCode", source = "partnerCode")
  @Mapping(target = "riskLevel", source = "riskLevel")
  @Mapping(target = "insurance", source = "insurance")
  @Mapping(target = "suggestedAmount", source = "suggestedAmount")
  @Mapping(target = "assignee", expression = "java(com.msb.bpm.approval.appr.util.SecurityContextUtil.getCurrentUser())")
  @Mapping(target = "generatorStatus", expression = "java(com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums.DEFAULT.getValue())")
  @Mapping(target = "processingStepCode", expression = "java(com.msb.bpm.approval.appr.enums.application.ProcessingStep.MAKE_PROPOSAL.getCode())")
  @Mapping(target = "processingRole", expression = "java(com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM.name())")
  @Mapping(target = "regulatoryCode", source = "regulatoryCode")
  @Mapping(target = "distributionFormCode", source = "distributionFormCode")
  @Mapping(target = "distributionForm", source = "distributionForm")
  @Mapping(target = "oldId", source = "id")
  ApplicationEntity copyApplicationEntity(ApplicationEntity applicationEntity);


  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  ApplicationContactEntity copyApplicationContactEntity(
      ApplicationContactEntity applicationContactEntity);

  Set<ApplicationContactEntity> copyApplicationContactEntities(
      Set<ApplicationContactEntity> applicationContactEntities);

  PostCmsV2CreateApplicationRequest convertFeedbackToCmsV2Request(ApplicationFbDTO applicationFbDTO);
}
