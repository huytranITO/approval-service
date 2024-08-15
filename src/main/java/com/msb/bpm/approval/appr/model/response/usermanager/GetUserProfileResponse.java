package com.msb.bpm.approval.appr.model.response.usermanager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/6/2023, Wednesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class GetUserProfileResponse {

  private Personal personal;

  private User user;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @JsonInclude(Include.NON_NULL)
  @Builder
  public static class Personal {

    private String email;
    private String phoneNumber;
    private String fullName;
    private User user;
    private String shortName;
    private Long id;
    private String uniqueId;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class User {
    private Long id;
    private String bdsId;
    private String email;
    private Boolean enabled;
    private String phoneNumber;
    private String username;
    private String uniqueId;
  }
}
