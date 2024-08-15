package com.msb.bpm.approval.appr.model.request.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateAssetRequest.AssetInfo;
import com.msb.bpm.approval.appr.validator.CmsIDConstraint;
import com.msb.bpm.approval.appr.validator.MaxCreditCardConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/8/2023, Saturday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@CmsIDConstraint.List({
    @CmsIDConstraint(
        object = "customer"
    ),
    @CmsIDConstraint(
        object = "customerRelations"
    ),
    @CmsIDConstraint(
        object = "enterpriseRelations"
    ),
    @CmsIDConstraint(
        object = "applicationIncomes"
    ),
    @CmsIDConstraint(
        object = "applicationCredits"
    )
})
@MaxCreditCardConstraint
public class PostCmsV2CreateApplicationRequest implements Serializable {

  @NotNull
  @Valid
  private CmsApplicationDTO application;

  @NotNull
  @Valid
  private CmsCustomerDTO customer;

  @Valid
  private List<CmsCustomerRelationDTO> customerRelations;

  @Valid
  private List<CmsEnterpriseRelationDTO> enterpriseRelations = new ArrayList<>();

  @NotEmpty
  @Valid
  private List<CmsIncomeDTO> applicationIncomes;

  @NotEmpty
  @Valid
  private List<CmsBaseCreditDTO> applicationCredits;

  private boolean insurance = false;

  @Valid
  private List<CmsApplicationContactDTO> applicationContact;

  private List<AssetInfo> assetInfo = new ArrayList<>();

  @Size(max = 2000)
  private String desCreditAvailable;
}
