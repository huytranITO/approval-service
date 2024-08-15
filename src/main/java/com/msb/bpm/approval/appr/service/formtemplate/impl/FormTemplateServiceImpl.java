package com.msb.bpm.approval.appr.service.formtemplate.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.FORM_TEMPLATE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.FINISH_GENERATE_TEMPLATE;
import static com.msb.bpm.approval.appr.util.Util.YYYYMMDDHHMMSS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.aop.AopProxy;
import com.msb.bpm.approval.appr.client.checklist.ChecklistClient;
import com.msb.bpm.approval.appr.client.formtemplate.FormTemplateClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.formtemplate.FormTemplateApplicationMapper;
import com.msb.bpm.approval.appr.mapper.formtemplate.FormTemplateChecklistMapper;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.dto.formtemplate.ApplicationFormTemplateDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateChecklistRequestDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateFileRequestDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.GenerateFormDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.AsyncCreateTemplateResultDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistFileMessageDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistMessageResponseDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateResponseDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationExtraDataEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.notication.NoticeType;
import com.msb.bpm.approval.appr.notication.NotificationFactory;
import com.msb.bpm.approval.appr.notication.NotificationInterface;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.application.impl.PostDebtInfoServiceImpl;
import com.msb.bpm.approval.appr.service.applicationListenerEvent.events.SentCompleteGenerateFormTemplateEvent;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.formtemplate.FormTemplateService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class FormTemplateServiceImpl implements FormTemplateService {

  private final FormTemplateClient formTemplateClient;
  private final FormTemplateApplicationMapper formTemplateApplicationMapper;
  private final ChecklistServiceImpl checklistServiceImpl;
  private final ChecklistClient checklistClient;
  private final FormTemplateChecklistMapper formTemplateChecklistMapper;
  private final ObjectMapper objectMapper;
  private final ApplicationRepository applicationRepository;
  private final ApplicationConfig applicationConfig;
  private final PostDebtInfoServiceImpl postDebtInfoService;
  private final AopProxy aopProxy;
  private final NotificationFactory factory;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void generateFormTemplate (
      ApplicationEntity entity,
      String phaseCode,
      RuleTemplateResponseDTO ruleTemplateResponse,
      String purpose) {

    log.info(" +++ ruleTemplate Response: [{}]", ruleTemplateResponse.getData());
    List<FormTemplateChecklistRequestDTO> listChecklistDto = new ArrayList<>();

    ChecklistBaseResponse<GroupChecklistDto> checklistResponse = checklistClient.getChecklistByRequestCode(entity.getBpmId());
    log.info("checklist response: [{}]", checklistResponse);

    ApplicationFormTemplateDTO requestDTO = formTemplateApplicationMapper.buildApplicationFormTemplate(entity);

    try {
      log.info("RequestDTO: [{}]", objectMapper.writeValueAsString(requestDTO));
    } catch (Exception e) {
      log.error("Can not build data for generate template: {}", e.getMessage());
      throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
    }

    ruleTemplateResponse.getData().getData().stream()
        .forEach(detail -> {
              ChecklistDto checklistDto = Util.getChecklistMappingId(checklistResponse, detail.getChecklistCode().getValue());

              if (!Objects.isNull(checklistDto)) {
                if (!Objects.isNull(detail.getDocNumber())
                    && !Objects.isNull(detail.getDocNumber().getValue())) {
                  String documentNo = Util.buildDocumentNo(entity, detail.getDocNumber().getValue());
                  log.info("Document number: [{}]", documentNo);
                  requestDTO.getInitializeInfo().getApplication().setDocumentNo(documentNo);
                  entity.setApprovalDocumentNo(documentNo);
                }

                checklistDto.setIsError(false);
                checklistDto.setIsGenerated(true);
                FormTemplateChecklistRequestDTO formChecklistRequest =
                    formTemplateChecklistMapper.toFormTemplateChecklistRequestDTO(checklistDto);
                List<FormTemplateFileRequestDTO> listFiles = new ArrayList<>();

                FormTemplateFileRequestDTO fileGenerator =
                    FormTemplateFileRequestDTO.builder()
                        .code(detail.getTemplateCode().getValue())
                        .createdBy(SecurityContextUtil.getUsername())
                        .build();


                List<String> templateList = Arrays.asList("MB0701","MB070201","MB070202");
                if (detail.getTemplateCode().getValue() != null && templateList.contains(detail.getTemplateCode().getValue())) {
                  fileGenerator.setReturnType("DOCX");
                }

                // check replace new file
                if (!Objects.isNull(checklistDto.getListFile())) {
                  checklistDto.getListFile().stream()
                      .filter(fileDto -> !Objects.isNull(fileDto.getProps()) && detail
                                      .getTemplateCode()
                                      .getValue()
                                      .equalsIgnoreCase(fileDto.getProps().get("templateCode")))
                      .findFirst()
                      .ifPresent(fileDto -> fileGenerator.setId(fileDto.getId()));
                }

                listFiles.add(fileGenerator);
                formChecklistRequest.setFiles(listFiles);
                formChecklistRequest.setJsonData(JsonUtil.convertObject2String(requestDTO, objectMapper));
                listChecklistDto.add(formChecklistRequest);
              }
            });
    try {
      if(CollectionUtils.isEmpty(listChecklistDto)){
        throw new ApprovalException(DomainCode.NOT_FOUND_CHECKLIST_ERROR);
      }
      GenerateFormDTO generateFormDTO =
          GenerateFormDTO.builder()
              .requestCode(entity.getBpmId())
              .listChecklist(listChecklistDto)
              .phaseCode(phaseCode)
              .build();
      log.info("generateFormDTO: {}", objectMapper.writeValueAsString(generateFormDTO));

      formTemplateClient.getDocuments(HeaderUtil.getToken(), generateFormDTO);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateTemplateChecklist(String generateResult) {
    try {
      AsyncCreateTemplateResultDTO generateResultDTO =
          objectMapper.readValue(generateResult, AsyncCreateTemplateResultDTO.class);

      Optional<ApplicationEntity> entityOps =
          applicationRepository.findByBpmId(generateResultDTO.getRequestCode());
      if (!entityOps.isPresent()
          || !GeneratorStatusEnums.PROCESSING
              .getValue()
              .equalsIgnoreCase(entityOps.get().getGeneratorStatus())) {
        log.error("+++ Application does not exist or process status is not convention");
        return;
      }
      ApplicationEntity applicationEntity = entityOps.get();

      log.info("generate result converted: [{}]", generateResultDTO);
      if (!generateResultDTO.isUploadDocStatus()) {
        log.error("generate failure: [{}]", generateResultDTO.getMessage());
        applicationEntity.setGeneratorStatus(GeneratorStatusEnums.DEFAULT.getValue());

        // notification
        NotificationInterface notificationInterface = factory.getNotification(NoticeType.FORM_TEMPLATE);
        notificationInterface.alert(applicationEntity.getBpmId(), applicationEntity.getAssignee());
        return;
      }

      CreateChecklistRequest checklistRequest =
          CreateChecklistRequest.builder()
              .businessFlow(BusinessFlow.BO_USL_NRM.name())
              .phaseCode(generateResultDTO.getPhaseCode())
              .requestCode(generateResultDTO.getRequestCode())
              .listChecklist(
                  updateFileName(
                      formTemplateChecklistMapper.toListChecklistDto(
                          generateResultDTO.getListChecklist()),
                      applicationEntity.getBpmId()))
              .build();

      log.info(
          "Save checklist: [{}]", JsonUtil.convertObject2String(checklistRequest, objectMapper));

      Object saveChecklist =
          checklistServiceImpl.uploadFileChecklistInternal(
              checklistRequest, applicationConfig.getJwt().buildBase64());
      log.info("Checklist saved: [{}]", saveChecklist);

      // Lưu danh sách form template đã được generate thành công vào hồ sơ
      Set<ChecklistFileMessageDTO> listFile =
          generateResultDTO.getListChecklist().stream()
              .map(ChecklistMessageResponseDTO::getChecklistFiles)
              .flatMap(List::stream)
              .collect(Collectors.toSet());

      List<String> listFileNames =
          listFile.stream().map(ChecklistFileMessageDTO::getFileName).collect(Collectors.toList());

      removeDuplicateExtraData(applicationEntity);

      Map<Pair<String, String>, ApplicationExtraDataEntity> extraDataEntityMap =
          applicationEntity.getExtraDatas().stream()
              .collect(
                  Collectors.toMap(
                      ext -> new ImmutablePair<>(ext.getCategory(), ext.getKey()), ext -> ext, (existing, replacement) -> existing));

      postDebtInfoService.referenceExtraDataEntities(
          applicationEntity, listFile, extraDataEntityMap, DOCUMENTS_INFO, FORM_TEMPLATE);
      applicationEntity.setGeneratorStatus(GeneratorStatusEnums.DONE.getValue());
      applicationRepository.save(applicationEntity);

      ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
      metadata.put(APPLICATION, applicationEntity);
      metadata.put("DOCUMENT_CODE", String.join(",", listFileNames));

      // notification
      NotificationInterface notificationInterface = factory.getNotification(NoticeType.FORM_TEMPLATE);
      notificationInterface.notice(applicationEntity.getBpmId(), applicationEntity.getAssignee());

      aopProxy.sendEmail(FINISH_GENERATE_TEMPLATE.name(), metadata);

      applicationEventPublisher.publishEvent(new SentCompleteGenerateFormTemplateEvent(this, applicationEntity.getBpmId(), applicationEntity.getAssignee()));

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private List<ChecklistDto> updateFileName(List<ChecklistDto> listChecklistDto, String bpmId) {
    listChecklistDto.stream()
        .filter(checklistDto -> CollectionUtils.isNotEmpty(checklistDto.getListFile()))
        .forEach(
            checklistDto ->
                checklistDto.getListFile().stream()
                    .forEach(
                        fileDto ->
                            fileDto.setFileName(
                                fileDto
                                        .getFileName()
                                        .substring(0, fileDto.getFileName().lastIndexOf("."))
                                    + "_"
                                    + fileDto.getCreatedBy()
                                    + "_"
                                    + DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS)
                                        .format(LocalDateTime.now()))));
    return listChecklistDto;
  }

  private static void removeDuplicateExtraData(ApplicationEntity applicationEntity) {
    if (applicationEntity.getExtraDatas() != null) {
      Map<String, Long> duplicateCountMap = applicationEntity.getExtraDatas().stream()
              .collect(Collectors.groupingBy(
                      extraData -> extraData.getKey() + extraData.getCategory(),
                      Collectors.counting()
              ));

      List<String> duplicatesData = duplicateCountMap.entrySet().stream()
              .filter(entry -> entry.getValue() > 1)
              .map(Map.Entry::getKey)
              .collect(Collectors.toList());

      if (ObjectUtils.isNotEmpty(duplicatesData)) {
        List<ApplicationExtraDataEntity> dataSort = new ArrayList<>(applicationEntity.getExtraDatas());
        Comparator<ApplicationExtraDataEntity> comp = (ApplicationExtraDataEntity a, ApplicationExtraDataEntity b) -> b.getId().compareTo(a.getId());
        dataSort.sort(comp);
        //
        HashMap<String, ApplicationExtraDataEntity> mapData = new HashMap<>();

        for (ApplicationExtraDataEntity itExtraData : dataSort) {
          String key = itExtraData.getKey() + itExtraData.getCategory();
          if (!mapData.containsKey(key)) {
            mapData.put(key, itExtraData);
          } else {
            // Xóa bản ghi duplicate
            applicationEntity.removeExtraData(itExtraData);
          }
        }
      }
    }
  }
}
