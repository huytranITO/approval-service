package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityRequestMapDTO;
import com.msb.bpm.approval.appr.model.dto.rule.TransitionConditionDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.rule.RuleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 **/
@Mapper
public interface DecisionRuleMapping {

  DecisionRuleMapping INSTANCE = Mappers.getMapper(DecisionRuleMapping.class);

  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "riskLevel", constant = "false")
  @Mapping(target = "givebackRole", expression = "java(com.msb.bpm.approval.appr.util.Util.checkRole(applicationEntity.getGivebackRole()))")
  @Mapping(target = "customerType", expression = "java(applicationEntity.getCustomer().getCustomerType())")
  TransitionConditionDTO toTransitionCondition(ApplicationEntity applicationEntity);

//  @Mapping(target = "segmentType", source = "transitionConditionDTO.segment")
  @Mapping(target = "submissionType", source = "transitionConditionDTO.submissionPurpose")
  @Mapping(target = "authorityLevel", source = "transitionConditionDTO.priorityAuthority")
  @Mapping(target = "approveResult", expression = "java(com.msb.bpm.approval.appr.util.Util.getFinalApprovalResult(transitionConditionDTO.getCredits()))")
  @Mapping(target = "riskType", source = "riskLevel")
  @Mapping(target = "isApprovalLevel", expression = "java(com.msb.bpm.approval.appr.enums.application.ProcessingRole.isOrganizationStep(transitionConditionDTO.getProcessingRole()))")
  RuleRequest toRuleRequest(TransitionConditionDTO transitionConditionDTO);

  @Mapping(target = "applicationId", source = "applicationEntity.id")
  @Mapping(target = "applicationBpmId", source = "applicationEntity.bpmId")
  @Mapping(target = "creditRiskType", constant = "false")
  @Mapping(target = "customerType", source = "customerDTO.customerType")
  @Mapping(target = "customerSegment", expression = "java(com.msb.bpm.approval.appr.util.Util.checkCustomerSegment(customerDTO))")
  AuthorityRequestMapDTO toAuthorityRequestMap(ApplicationEntity applicationEntity, CustomerDTO customerDTO);

  @Mapping(target = "applicationId", source = "authorityRequestMapDTO.applicationBpmId")
  @Mapping(target = "approvalFlow", source = "authorityRequestMapDTO.approvalType")
  @Mapping(target = "customerType", expression = "java(com.msb.bpm.approval.appr.enums.application.CustomerType.valueOf(authorityRequestMapDTO.getCustomerType()))")
  @Mapping(target = "customerGroup", source = "authorityRequestMapDTO.customerSegment")
  @Mapping(target = "segmentCustomer", source = "authorityRequestMapDTO.segment")
  @Mapping(target = "riskGroup", expression = "java(com.msb.bpm.approval.appr.util.Util.checkRiskGroup(authorityRequestMapDTO))")
  @Mapping(target = "authorization", expression = "java(com.msb.bpm.approval.appr.util.Util.checkAdditionAuthorization(authorityRequestMapDTO))")
  AuthorityCheckerRequest toAuthorityCheckerRequest(AuthorityRequestMapDTO authorityRequestMapDTO);

}
