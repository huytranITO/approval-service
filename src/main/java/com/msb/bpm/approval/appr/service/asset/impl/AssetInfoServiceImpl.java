package com.msb.bpm.approval.appr.service.asset.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralConfigProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.asset.CollateralEndpoint;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.checklist.DomainType;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.request.collateral.ChecklistAssetRequest;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.model.response.asset.AssetDataResponse;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistDataResponse;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.asset.AssetInfoService;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetInfoServiceImpl extends AbstractBaseService implements AssetInfoService {
    private final CollateralConfigProperties configProperties;
    private final CollateralClient collateralClient;
    private final ObjectMapper objectMapper;

    private final ChecklistService checklistService;
    private final ApplicationDraftRepository applicationDraftRepository;

    @Override
    public Object upsertAsset(PostAssetInfoRequest req) {
        log.info("START call api upsert asset info with request: {}", JsonUtil.convertObject2String(req, objectMapper));
        AssetResponse responseDataAsset = callUpsertAssetCommon(req);

        // reload checklist
        AssetRuleChecklistDataResponse dataChecklist = getAssetRuleChecklistData(req);

        // get reload checklist asset
        ChecklistAssetRequest checklistAssetRequest = new ChecklistAssetRequest();
        checklistAssetRequest.setBpmId(req.getBpmId());
        checklistAssetRequest.setAssetInfoRequests(dataChecklist.getData());
        ChecklistBaseResponse<GroupChecklistDto> checklists = (ChecklistBaseResponse<GroupChecklistDto>) checklistService.reloadChecklistAsset(checklistAssetRequest);

        if (ObjectUtils.isNotEmpty(dataChecklist.getData())) {
            //lấy checklist định giá tài sản
            List<ChecklistDto> listChecklist = checklists.getData().getListChecklist()
                    .stream()
                    .filter(checklistDto -> checklistDto.getCode().equals(ApplicationConstant.RB_BPM_CHECKLIST))
                    .collect(Collectors.toList());

            //map file ecm
            mapFileChecklistAsset(dataChecklist, listChecklist);

            // upload file to checklist
            uploadFile2Checklist(req, listChecklist);
        }

        AssetCommonInfoDTO dto = objectMapper.convertValue(responseDataAsset.getData(), AssetCommonInfoDTO.class);
        dto.setCompleted(Boolean.TRUE);
        dto.setType(req.getType());
        dto.setBpmId(req.getBpmId());
        log.info("END call api upsert asset info with response: {}", JsonUtil.convertObject2String(dto, objectMapper));
        // Kiểm tra assetId luồng LDP có bị thay đổi không
        checkChangeAssetIdForLDP(req.getBpmId(), responseDataAsset, checklists);
        return dto;
    }

    private void checkChangeAssetIdForLDP(String bpmId, AssetResponse responseDataAsset, ChecklistBaseResponse<GroupChecklistDto> newChecklists) {
        try {
            LinkedHashMap linkedHashMap = (LinkedHashMap) responseDataAsset.getData();
            List<AssetDataResponse> lstAsset = objectMapper.convertValue(
                    linkedHashMap.get("assetData"),
                    new TypeReference<List<AssetDataResponse>>() {
                    }
            );
            if (ObjectUtils.isEmpty(lstAsset)) {
                return;
            }

            HashMap<Long, Long> assetIdMap = new HashMap<>();
            for (AssetDataResponse itData : lstAsset) {
                if (ObjectUtils.isNotEmpty(itData.getTempAssetId())) {
                    assetIdMap.put(itData.getId(), itData.getTempAssetId());
                }
            }

            ApplicationDraftEntity draftEntity = applicationDraftRepository
                    .findByBpmIdAndTabCodeAndStatus(bpmId, ApplicationConstant.TabCode.CHECKLIST_ASSET_INFO, UNFINISHED)
                    .orElse(null);

            if (draftEntity == null) {
                return;
            }

            List<ChecklistDto> oldChecklistAsset = objectMapper.readValue(
                    draftEntity.getData(),
                    new TypeReference<List<ChecklistDto>>() {
                    }
            );

            if (ObjectUtils.isNotEmpty(oldChecklistAsset) && ObjectUtils.isNotEmpty(assetIdMap)) {
                assetIdMap.forEach((newAssetId, oldAssetId) -> {
                    // Copy file checklist đã upload
                    List<ChecklistDto> listChecklistFileCopy = new ArrayList<>();

                    for (ChecklistDto oldChecklist : oldChecklistAsset) {
                        if (oldChecklist.getDomainObjectId().equals(oldAssetId)) {
                            // Lay thong tin tu checklist hien tai
                            if (newChecklists.getData() != null && ObjectUtils.isNotEmpty(newChecklists.getData().getListChecklist())) {
                                for (ChecklistDto newCheckList : newChecklists.getData().getListChecklist()) {
                                    if (newCheckList.getDomainType().equals(DomainType.COLLATERAL.name())
                                            && newCheckList.getDomainObjectId().equals(newAssetId)
                                            && newCheckList.getCode().equals(oldChecklist.getCode())
                                            && newCheckList.getGroupCode().equals(oldChecklist.getGroupCode())
                                    ) {
                                        newCheckList.setListFile(oldChecklist.getListFile());
                                        listChecklistFileCopy.add(newCheckList);
                                    }
                                }
                            }
                        }
                    }
                    if (ObjectUtils.isNotEmpty(listChecklistFileCopy)) {
                        CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
                                .requestCode(bpmId)
                                .businessFlow(BusinessFlow.BO_USL_NRM.name())
                                .phaseCode(PhaseCode.NRM_ALL_S.name())
                                .listChecklist(listChecklistFileCopy)
                                .build();
                        checklistService.uploadFileChecklist(createChecklistRequest);
                        // Update draft status
                        draftEntity.setStatus(FINISHED);
                        applicationDraftRepository.save(draftEntity);
                    }
                });
            }
        } catch (Exception e) {
            log.error("checkChangeAssetId data error: ", e);
        }

    }

    @NotNull
    private AssetResponse callUpsertAssetCommon(PostAssetInfoRequest req) {
        String endpointUpsert = UriComponentsBuilder
                .fromUriString(configProperties.getBaseUrl() + configProperties.getEndpoint()
                        .get(CollateralEndpoint.UPSERT_ASSET.getValue())
                        .getUrl())
                .toUriString();
        log.info("START call api upsert with uri: {}", endpointUpsert);
        AssetResponse response = collateralClient.callApiCommonAsset(req.getCollateral(), HttpMethod.PUT, endpointUpsert, AssetResponse.class);

        log.info("END call api upsert asset info: {}",  JsonUtil.convertObject2String(response, objectMapper));
        if(ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getData())) {
            throw new ApprovalException(DomainCode.CMS_CREATE_ASSET_ERROR);
        }
        return response;
    }

    @NotNull
    private AssetRuleChecklistDataResponse getAssetRuleChecklistData(PostAssetInfoRequest req) {
        // reload checklist
        String endpointChecklist = UriComponentsBuilder
                .fromUriString(configProperties.getBaseUrl() + configProperties.getEndpoint()
                        .get(CollateralEndpoint.COMMON_GET_ASSET.getValue())
                        .getUrl())
                .toUriString() + req.getBpmId();
        log.info("START call api get asset checklist with uri: {}", endpointChecklist);
        AssetRuleChecklistDataResponse dataChecklist = collateralClient.callApiCommonAsset(null, HttpMethod.GET, endpointChecklist, AssetRuleChecklistDataResponse.class);
        log.info("END call api upsert asset info: {}",  JsonUtil.convertObject2String(dataChecklist, objectMapper));

        if(ObjectUtils.isEmpty(dataChecklist)) {
            throw new ApprovalException(DomainCode.ASSET_ERROR,new Object[]{"Lỗi lấy thông tin checklist tài sản"});
        }
        return dataChecklist;
    }

    private void uploadFile2Checklist(PostAssetInfoRequest req, List<ChecklistDto> listChecklist) {
        CreateChecklistRequest createChecklistRequest =
                CreateChecklistRequest.builder()
                        .requestCode(req.getBpmId())
                        .businessFlow(BusinessFlow.BO_USL_NRM.name())
                        .phaseCode(PhaseCode.NRM_ALL_S.name())
                        .listChecklist(listChecklist.stream()
                                .filter(checklistDto -> CollectionUtils.isNotEmpty(checklistDto.getListFile()))
                                .collect(Collectors.toList()))
                        .build();
        log.info("START upload file asset to checklist with request: {}", JsonUtil.convertObject2String(createChecklistRequest, objectMapper));
        Object dataUploadCL = checklistService.uploadFileChecklist(createChecklistRequest);
        log.info("END upload file asset to checklist with response: {}", JsonUtil.convertObject2String(createChecklistRequest, objectMapper));
        if(ObjectUtils.isEmpty(dataUploadCL)) {
            throw new ApprovalException(DomainCode.CHECKLIST_ERROR);
        }
    }

    private static void mapFileChecklistAsset(AssetRuleChecklistDataResponse dataChecklist, List<ChecklistDto> listChecklist) {
        listChecklist.forEach(checklistDto -> {
            AssetRuleChecklistResponse lstFileUploadData = dataChecklist.getData().stream()
                    .filter(dt -> Long.compare(dt.getAssetId(), checklistDto.getDomainObjectId()) == 0)
                    .findFirst()
                    .orElse(null);
                if(ObjectUtils.isNotEmpty(lstFileUploadData)
                    && ObjectUtils.isNotEmpty(lstFileUploadData.getFileInfo())){
                Set<String> fileNamesInChecklist = new HashSet<>();

                if (CollectionUtils.isNotEmpty(checklistDto.getListFile())) {
                    fileNamesInChecklist.addAll(checklistDto.getListFile().stream()
                            .map(FileDto::getFileName)
                            .collect(Collectors.toList()));
                }

                List<FileDto> uniqueFiles = lstFileUploadData.getFileInfo().stream()
                        .filter(fileDto -> !fileNamesInChecklist.contains(fileDto.getFileName())
                                && ObjectUtils.isNotEmpty(fileDto.getMinioPath())
                                && ObjectUtils.isNotEmpty(fileDto.getFileSize()))
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(uniqueFiles)) {
                    if (CollectionUtils.isEmpty(checklistDto.getListFile())) {
                        checklistDto.setListFile(uniqueFiles);
                    } else {
                        checklistDto.getListFile().addAll(uniqueFiles);
                    }
                }
            }
        });
    }
}
