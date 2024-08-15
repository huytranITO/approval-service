package com.msb.bpm.approval.appr.model.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.request.asset.AssetInforRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationFbDTO implements Serializable {

  private CmsApplicationDTO application;

  private CmsCustomerDTO customer;

  private List<CmsCustomerRelationDTO> customerRelations;

  private List<CmsEnterpriseRelationDTO> enterpriseRelations = new ArrayList<>();

  private List<CmsIncomeDTO> applicationIncomes;

  private List<CmsBaseCreditDTO> applicationCredits;

  private boolean insurance = false;

  private List<CmsApplicationContactDTO> applicationContact;

  private List<AssetInforRequest> assetInfo;

  private String desCreditAvailable;
}
