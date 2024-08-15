package com.msb.bpm.approval.appr.client.usermanager.v2.config.response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class GetUserResponse {
  private Data data;
  private String code;
  private String message;

  @Getter
  @Setter
  @ToString
  public static class Data {
    private String dob;
    private String email;
    private String gender;
    private String phoneNumber;
    private String fullName;
    private String employeeId;
    private String uniqueId;
    private String id;
    private String username;
    private Boolean enabled;
    private List<String> modules;
  }
}
