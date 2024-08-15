package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateSubCreditCardDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;import java.util.List;
import java.util.Set;import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateCreditDTO implements Serializable {
  private static final long serialVersionUID = 1140324566034076969L;

  private String id = "null";
  private String appCreditLoanId = "null";
  //beginregion loan
  private String creditType = "null";
  private String loanCreditTypeValue = "null";//loai khoan vay no
  private String loanDocumentName = "null";
  private String loanApproveResult = "null";
  private String loaApproveResultValue = "null";
  private String loanTypeAmount = "null";
  private String loanTotalCapital = "null";
  private String loanEquityCapital = "null";
  private String loanPurpose = "null";
  private String loanTypePurposeValue = "null";
  private String loanPurposeExplanation = "null";
  private String loanPayback;
  private String loanLtd = "null";
  private String loanCreditForm = "null";
  private String loanCreditFormValue = "null";
  private String loanDisburseFrequency = "null";
  private String loanDisburseFrequencyValue = "null";
  private String loanPeriod = "null";
  private String debtPayMethod = "null";
  private String loanDebtPayMethodValue = "null";
  private String principalPayPeriod = "null";
  private String principalPayUnit = "null";
  private String principalPayUnitValue = "null";
  private String interestPayPeriod = "null";
  private String interestPayUnit = "null";
  private String interestPayUnitValue = "null";
  private String interestRateCode = "null";
  private String disburseMethod = "null";
  private String disburseMethodValue = "null";
  private String disburseMethodExplanation = "null";
  private String kunnPeriod = "null";
  private String loanOriginalPeriod = "null";
  private String loanAssetType = "null";
  private String loanAssetInfo = "null";
  private String loanAssetDoc = "null";
  private String loanGuaranteeFormValue = "null";
  private String loanProductName = "null";
  private String loanProductInfoName = "null";
  private List<FormTemplateCreditAssetDTO> loanAssetName = new ArrayList<>();
//endregion loan
//startregion overdraft

  private String overdraftCreditTypeValue = "null"; //loai khoan vay thau chi
  private String overdraftDocumentName = "null";// ma van ban thau chi
  private String overdraftAmount = "null";
  private String overdraftLimitSustentivePeriod = "null";
  private String overdraftPurposeValue = "null";
  private String overdraftDebtPayMethodValue = "null";
  private String overdraftAssetType = "null";
  private String overdraftAssetInfo = "null";
  private String overdraftAssetDoc = "null";
  private String infoAdditional = "null";
  private String overdraftCreditFormValue = "null";
  private String overdraftApproveResultValue = "null";
  private String overdraftGuaranteeFormValue = "null";
  private String overdraftProductName = "null";
  private String overdraftProductInfoName = "null";
  private List<FormTemplateCreditAssetDTO> overAssetName = new ArrayList<>();
//endregion overdraft
//startregion card

  private String cardCreditTypeValue = "null"; //loai khoan vay the
  private String cardDocumentName = "null";// ma van ban vay the
  private String primaryCardLimit = "null";
  private String cardTypeValue = "null";
  private String cardLimitSustentivePeriod = "null";
  private String cardPolicyCode = "null";
  private String cardAssetType = "null";
  private String cardAssetInfo = "null";
  private String cardAssetDoc = "null";
  private String cardCreditFormValue = "null";
  private String cardApproveResultValue = "null";
  private String cardGuaranteeFormValue = "null";
  private String cardProductName = "null";
  private List<FormTemplateCreditAssetDTO> cardAssetName = new ArrayList<>();

  private boolean hasSubCard = false;
  private Set<FormTemplateSubCreditCardDTO> subCreditCards = new HashSet<FormTemplateSubCreditCardDTO>();
}
