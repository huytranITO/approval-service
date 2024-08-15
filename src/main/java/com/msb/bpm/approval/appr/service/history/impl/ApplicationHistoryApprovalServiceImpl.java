package com.msb.bpm.approval.appr.service.history.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.authority.AuthorityStatus;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.ApplicationApprovalHistoryMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationApprovalHistoryDTO;
import com.msb.bpm.approval.appr.model.dto.application.HistoryApprovalDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.ApplicationHistoryApprovalRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.history.ApplicationHistoryApprovalService;
import com.msb.bpm.approval.appr.service.intergated.AuthorityService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Mai Dai Hai (OS)
 * @mailto : outsourcebpm27@msb.com.vn
 * @created : 02/06/2023, Friday
 **/
@Service
@AllArgsConstructor
@Slf4j
public class ApplicationHistoryApprovalServiceImpl implements ApplicationHistoryApprovalService {

  private final ApplicationHistoryApprovalRepository approvalRepository;
  private final AuthorityService authorityService;
  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;
  private final CommonService commonService;

  /**
   * Lấy lịch sử yêu cầu
   *
   * @param bpmId String
   * @return List<ApplicationApprovalHistoryDTO>
   */
  @Override
  @Transactional(readOnly = true)
  public List<ApplicationApprovalHistoryDTO> getApplicationApprovalHistory(String bpmId) {
    log.info("GET_APPROVAL_HISTORY BEGIN:: bpmId {}", bpmId);
    List<ApplicationHistoryApprovalEntity> approvalEntity = approvalRepository.findByApplicationBpmIdOrderByExecutedAtDesc(
        bpmId).orElseThrow(
        () -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    List<ApplicationApprovalHistoryDTO> applicationHistoryDTOS = ApplicationApprovalHistoryMapper.INSTANCE.toApprovalHistoryDtoList(
        approvalEntity);
    log.info("GET_APPROVAL_HISTORY END");
    return applicationHistoryDTOS;
  }

  /**
   * Lưu lịch sử yêu cầu
   *
   * @param historyApprovalDTO HistoryApprovalDTO
   */
  @Override
  @Transactional
  public void saveHistoryApproval(HistoryApprovalDTO historyApprovalDTO) {

    GetUserProfileResponse currentUser = commonService.getUserDetail(historyApprovalDTO.getUserName());

    GetUserProfileResponse proposalUser = commonService.getUserDetail(
        StringUtils.isNotBlank(historyApprovalDTO.getProposalApprovalUser())
            ? historyApprovalDTO.getProposalApprovalUser() : null);

    AuthorityDetailDTO authority = getAuthorityByCode(
        historyApprovalDTO.getProposalApprovalReception());

    ApplicationHistoryApprovalEntity historyApprovalEntity = ApplicationApprovalHistoryMapper.INSTANCE.toHistoryApprovalEntity(
            historyApprovalDTO)
        .withFullName(currentUser == null ? null : currentUser.getPersonal().getFullName())
        .withExecutedAt(LocalDateTime.now())
        .withApplication(applicationRepository.getReferenceById(historyApprovalDTO.getApplicationId()))
        .withProposalApprovalReceptionTitle(authority == null ? null : authority.getTitle())
        .withProposalApprovalFullName(
            proposalUser == null ? null : proposalUser.getPersonal().getFullName());

    approvalRepository.save(historyApprovalEntity);

    log.info("Saved new history approval : {}", JsonUtil.convertObject2String(historyApprovalDTO, objectMapper));

  }



  private AuthorityDetailDTO getAuthorityByCode(String authorityCode) {
    if (StringUtils.isBlank(authorityCode)) {
      return null;
    }

    try {
      return authorityService.filterAuthority(null, null, CustomerType.RB.name(),
              AuthorityStatus.ACTIVE.name())
          .stream()
          .filter(authority -> authority.getCode().equalsIgnoreCase(authorityCode))
          .findFirst()
          .orElse(null);
    } catch (Exception e) {
      log.error("Get authorities error : ", e);
      return null;
    }
  }
}
