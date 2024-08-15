package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Collateral {
  private Long approvalCollateralId;
  private String blockadeDisbursementPlan;
  private Boolean blocked;
  private Integer index;
  private String subType;
  private String certificateBookNo;
  private String certificateInName;
  private String certificateIssuedAt;
  private String certificateIssuedNo;
  private String certificateIssuedPlace;
  private String certificateName;
  private String certificateType;
  private String contactDetail;
  private String expertiseValue;
  private String mvalueId;
  private String name;
  private String subName;
  private String notaryAt;
  private String notaryOffice;
  private String notaryRegisterPlace;
  private String notaryUser;
  private String notaryUserPhone;
  private String notaryRMPhone;
  private String ownerDesc;
  private String ownerNum;
  private String propertyTypeDetail;
  private String requestDrafting;
  private String type;
  private String code;
  private String value;
  private String collateralDetail;
  private String isNew;
}
