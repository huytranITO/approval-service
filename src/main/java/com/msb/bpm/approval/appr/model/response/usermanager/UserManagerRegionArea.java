package com.msb.bpm.approval.appr.model.response.usermanager;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserManagerRegionArea {

  private String code;
  private String message;
  private DataResponse data;
  @Data
  public static class DataResponse {
    private String userCode;

    private String specializedBank;

    private String createdFullName;

    private String createdPhoneNumber;

    private OrganizationTreeDetail regionDetail;

    private OrganizationTreeDetail businessUnitDetail;

    private List<OrganizationTreeDetail> regionDetails;

    private List<OrganizationTreeDetail> businessUnitDetails;
  }
}
