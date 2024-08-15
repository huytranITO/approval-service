package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CLOSE_APP;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.CloseGroupEvent;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.util.CamundaUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/5/2023, Monday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PostCloseApplicationServiceImpl implements
    BaseService<ApplicationEntity, PostCloseApplicationRequest> {

  private final ApplicationRepository applicationRepository;
  private final CamundaService camundaService;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public String getType() {
    return POST_CLOSE_APP;
  }

  @Override
  @Transactional
  public ApplicationEntity execute(PostCloseApplicationRequest postCloseApplicationRequest, Object... obj) {
    String bpmId = (String) obj[0];

    log.info("PostCloseApplicationServiceImpl.execute() start with bpmId : {} , request : {}", bpmId,
        JsonUtil.convertObject2String(postCloseApplicationRequest, objectMapper));

    return Optional.of(applicationRepository.findByBpmId(bpmId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())) {
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          if (isComplete(applicationEntity.getStatus())) {
            throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
          }

          Map<String, VariableValueDto> returnVar = camundaService.completeTaskWithReturnVariables(
              applicationEntity, "close");

          applicationEntity.setPreviousRole(null);
          applicationEntity.setStatus(CamundaUtil.getStatus(returnVar));
          applicationRepository.saveAndFlush(applicationEntity);

          log.info(
              "PostCloseApplicationServiceImpl.execute() end with bpmId : {} , camunda task response : {}",
              bpmId, returnVar);

          if (isComplete(applicationEntity.getStatus())) {
                log.info("START SmartChat CloseGroup Event with applicationId={}",
                        applicationEntity.getBpmId());
                applicationEventPublisher.publishEvent(new CloseGroupEvent(this, applicationEntity.getBpmId()));
          }

          return applicationEntity;

        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }

}
