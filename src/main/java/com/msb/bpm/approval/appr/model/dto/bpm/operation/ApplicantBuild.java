package com.msb.bpm.approval.appr.model.dto.bpm.operation;

import com.msb.bpm.approval.appr.enums.bpm.RelationType;
import com.msb.bpm.approval.appr.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class ApplicantBuild {
  private ApplicationEntity app;
  private CustomerEntity cust;
  private CustomerAddressEntity address;
  private CustomerIdentityEntity identity;
  private IndividualCustomerEntity individual;
  private CustomerRelationShipEntity relation;
  private RelationType relationType;
  private boolean owner;
  private Integer index;
}