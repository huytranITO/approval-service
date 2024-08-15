package com.msb.bpm.approval.appr.service.checklist;

import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.DeleteChecklistGroupRequest;
import com.msb.bpm.approval.appr.model.request.checklist.GetChecklistRequest;
import com.msb.bpm.approval.appr.model.request.checklist.UpdateAdditionalDataRequest;
import com.msb.bpm.approval.appr.model.request.collateral.ChecklistAssetRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;

import java.util.List;

public interface ChecklistService {

  Object uploadFileChecklist(CreateChecklistRequest request);

  Object uploadFileChecklistInternal(CreateChecklistRequest request, String basicAuthToken);

  Object generateChecklist(GetChecklistRequest request);
  Object reloadChecklist(String bpmId, Object... obj);
  Object reloadChecklistCIC(String bpmId, List<CicEntity> cicEntities);
  void updateAdditionalData(UpdateAdditionalDataRequest request);
  void deleteChecklistGroup(DeleteChecklistGroupRequest request);
  void updateChecklistVersion(Long applicationId);
  Object getHistoryFile(Long checkListMappingId);
  Object deleteFile(Long id);
  Object getAllGroup();
  Object generateChecklist(ApplicationEntity entityApp);
  Object getPreSignedUpload(String bpmId, String fileName);
  Object getPreSignedDownload(String bpmId, String filePath);
  Long getSizeByFilePathMinIO(String filePath);
  ChecklistBaseResponse<GroupChecklistDto> getChecklistByRequestCode(String requestCode);
  Object generateChecklistFromFE(String bpmId);
  Object reloadChecklistAsset(ChecklistAssetRequest checklistAssetRequest);
}
