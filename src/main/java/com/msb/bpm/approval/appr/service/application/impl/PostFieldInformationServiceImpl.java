package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_FIELD_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationFieldInformationDTO;
import com.msb.bpm.approval.appr.model.dto.FieldInforDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PostFieldInformationServiceImpl implements
    BaseService<Object, PostFieldInformationRequest> {

  @Autowired
  private ApplicationRepository applicationRepository;

  @Override
  public String getType() {
    return POST_FIELD_INFO;
  }

  @Override
  @Transactional
  public Object execute(PostFieldInformationRequest request, Object... objects) {
    log.info("START with request={}, object={}", request, objects);
    String bpmId = Arrays.stream(objects).toArray()[0].toString();
    ApplicationEntity entityApp = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    if (!SecurityContextUtil.getCurrentUser()
        .equalsIgnoreCase(entityApp.getAssignee())) {
      throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
    }

    if (isComplete(entityApp.getStatus())) {
      throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED, new Object[]{bpmId});
    }

    // add new records
    saveData(entityApp, request);
    Set<ApplicationFieldInformationDTO> lstResult = ApplicationFieldInformationMapper.INSTANCE.
        toFieldInformations(entityApp.getFieldInformations());

    log.info("END with request={}, object={}, lstResult={}", request, objects, lstResult);
    return new FieldInforDTO()
            .withFieldInformations(lstResult);
  }

  @Transactional
  public void saveData(ApplicationEntity entityApp,
      PostFieldInformationRequest request) {
    log.info("START with entityApp={}, request={}", entityApp, request);
    Set<ApplicationFieldInformationEntity> lstAppFieldInfo = entityApp.getFieldInformations();
    for (ApplicationFieldInformationDTO item : request.getFieldInformations()) {
      ApplicationFieldInformationEntity entityFI;
      // check if exist then update, others is insert
      Optional<ApplicationFieldInformationEntity> afiEntityOptional =
          lstAppFieldInfo.stream().filter(e -> null != item.getId()
              && item.getId().equals(e.getId())).findFirst();
      if (afiEntityOptional.isPresent()) {
        entityFI = afiEntityOptional.get();
        entityFI.setUpdatedAt(LocalDateTime.now());
      } else {
        entityFI = new ApplicationFieldInformationEntity().withApplication(entityApp);
      }
      entityFI.setPlaceType(item.getPlaceType());
      entityFI.setRelationship(item.getRelationship());
      entityFI.setCityCode(item.getCityCode());
      entityFI.setDistrictCode(item.getDistrictCode());
      entityFI.setWardCode(item.getWardCode());
      entityFI.setAddressLine(item.getAddressLine());
      entityFI.setPlaceTypeValue(item.getPlaceTypeValue());
      entityFI.setRelationshipValue(item.getRelationshipValue());
      entityFI.setTimeAt(item.getTimeAt());
      entityFI.setExecutor(item.getExecutor());
      entityFI.setResult(item.getResult());
      entityFI.setResultValue(item.getResultValue());
      entityFI.setCityValue(item.getCityValue());
      entityFI.setDistrictValue(item.getDistrictValue());
      entityFI.setWardValue(item.getWardValue());
      entityFI.setInstructor(item.getInstructor());
      entityFI.setOrderDisplay(item.getOrderDisplay());
      entityFI.setAddressLinkId(item.getAddressLinkId());

      lstAppFieldInfo.add(entityFI);
    }
    List<Long> lstNewId = request.getFieldInformations().stream().map(ApplicationFieldInformationDTO::getId)
        .filter(Objects::nonNull).collect(Collectors.toList());

    lstAppFieldInfo.removeAll(lstAppFieldInfo.stream().filter(e -> null != e.getId()
            && !lstNewId.contains(e.getId())).collect(Collectors.toList()));

    applicationRepository.saveAndFlush(entityApp);
    log.info("END with entityApp={}, request={}", entityApp, request);
  }
}
