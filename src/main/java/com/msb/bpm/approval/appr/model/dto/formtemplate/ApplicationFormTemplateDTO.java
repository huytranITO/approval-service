package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateFieldInforDTO;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationFormTemplateDTO implements Serializable {

  private FormTemplateInitializeInfoDTO initializeInfo;
  private FormTemplateFieldInforDTO fieldInfor;
  private FormTemplateDebtInfoDTO debtInfo;
  private String proposalSign;
  private String approvalSign;
//  private String createdFormFullname = SecurityContextUtil.getCurrentUser();
  private String createdFormFullname = "";
  private String currentDateTime = "";
//  private String currentDateTime = DateTimeFormatter.ofPattern(VN_DATE_FORMAT).format(LocalDateTime.now());
  private FormTemplateAssetInfoDTO assetInfo;
}
