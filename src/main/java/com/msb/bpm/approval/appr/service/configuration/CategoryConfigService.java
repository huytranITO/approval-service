package com.msb.bpm.approval.appr.service.configuration;

import com.msb.bpm.approval.appr.model.dto.cic.CicBranchData;
import java.util.Map;

public interface CategoryConfigService {

  Map<String, CicBranchData> getCICBranch(String token);
}
