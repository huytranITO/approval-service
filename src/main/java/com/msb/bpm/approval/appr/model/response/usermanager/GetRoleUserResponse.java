package com.msb.bpm.approval.appr.model.response.usermanager;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/6/2023, Monday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetRoleUserResponse {

  private List<UserRoleResponse> value;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class UserRoleResponse {

    private String fullName;

    private String username;

    private String email;

    private Set<String> authorities;

    private String status;
  }
}
