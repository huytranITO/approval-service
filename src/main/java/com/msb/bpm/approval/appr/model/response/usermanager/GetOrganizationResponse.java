package com.msb.bpm.approval.appr.model.response.usermanager;


import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
* @author: BaoNV2
* @since: 17/10/2023 1:52 PM
* @description:  
* @update:
*
* */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetOrganizationResponse {
  private String code;
  private String message;
  private Data data;

  @Getter
  @Setter
  @Builder
  public static class Data {
    private Integer page;
    private Integer totalPage;
    private Integer totalRecord;
    private List<Organization> organizations;
  }

  @Getter
  @Setter
  @Builder
  public static class Organization {
    private Integer id;
    private String code;
    private String name;
    private String fullName;
    private String type;
    private String address;
    private String parentCode;
    private String parentName;
    private String specializedBank;
    private String createdBy;
    private String enabled;
    private Date createdAt;
  }
}
