package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CHECK_LIST_TAB;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.CheckListTabDTO;
import com.msb.bpm.approval.appr.model.request.data.PostCheckListTabRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCheckListInfoServiceImpl extends AbstractBaseService implements
    BaseService<Object, PostCheckListTabRequest> {

  private final ApplicationRepository applicationRepository;

  @Override
  public String getType() {
    return POST_CHECK_LIST_TAB;
  }

  @Override
  @Transactional
  public Object execute(PostCheckListTabRequest request, Object... objects) {
    return Optional.of(applicationRepository.findByBpmId((String) objects[0]))
        .filter(Optional::isPresent).map(Optional::get).map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())
              || !ProcessingRole.PD_RB_RM.toString()
              .equals(applicationEntity.getProcessingRole())) {
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          if (isComplete(applicationEntity.getStatus())) {
            throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
          }

          applicationEntity.setRmCommitStatus(request.isRmCommitStatus());
          applicationRepository.save(applicationEntity);
          return new CheckListTabDTO().withRmCommitStatus(request.isRmCommitStatus());

        }).orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

  }


}
