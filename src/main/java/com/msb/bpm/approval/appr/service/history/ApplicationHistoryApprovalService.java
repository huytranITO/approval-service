package com.msb.bpm.approval.appr.service.history;

import com.msb.bpm.approval.appr.model.dto.ApplicationApprovalHistoryDTO;
import com.msb.bpm.approval.appr.model.dto.application.HistoryApprovalDTO;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import java.util.List;

/**
 * @author : Mai Dai Hai (OS)
 * @mailto : outsourcebpm27@msb.com.vn
 * @created : 02/06/2023, Friday
 **/

public interface ApplicationHistoryApprovalService {

  List<ApplicationApprovalHistoryDTO> getApplicationApprovalHistory(String bpmId);

  void saveHistoryApproval(HistoryApprovalDTO historyApprovalDTO);

}