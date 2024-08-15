package com.msb.bpm.approval.appr.service.cic.impl;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.dto.cic.CicBranchData;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.service.cic.BranchService;
import com.msb.bpm.approval.appr.service.configuration.CategoryConfigService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BranchServiceImpl implements BranchService {

  private static final CicBranchData CIC_BRANCH_DEFAULT = CicBranchData.builder()
      .cicBranchCode("0011")
      .cicDealingRoomCode("001101")
      .build();

  private final CategoryConfigService categoryConfigService;

  @Override
  public Map<String, CicBranchData> getCICBranch(String token) {
    return categoryConfigService.getCICBranch(token);
  }

  @Override
  public CicBranchData getCicBranchData(ApplicationEntity entityApp,
      String token) {
    if (!ProcessingRole.isCICRole(entityApp.getProcessingRole())) {
      return CIC_BRANCH_DEFAULT;
    }

    Map<String, CicBranchData> cicBranchDataMap = getCICBranch(token);
    CicBranchData cicBranchData = cicBranchDataMap.get(entityApp.getBusinessCode());
    if (cicBranchData == null) {
      return CIC_BRANCH_DEFAULT;
    }

    return cicBranchData;
  }


}
