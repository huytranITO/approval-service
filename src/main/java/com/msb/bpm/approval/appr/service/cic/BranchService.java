package com.msb.bpm.approval.appr.service.cic;

import com.msb.bpm.approval.appr.model.dto.cic.CicBranchData;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import java.util.Map;

public interface BranchService {

  Map<String, CicBranchData> getCICBranch(String token);

  CicBranchData getCicBranchData(ApplicationEntity entityApp, String token);
}
