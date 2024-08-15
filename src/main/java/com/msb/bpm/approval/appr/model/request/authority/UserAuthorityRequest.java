package com.msb.bpm.approval.appr.model.request.authority;


import com.msb.bpm.approval.appr.enums.authority.AuthorityClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuthorityRequest {
  private String proposalApprovalUser;
  private AuthorityClass authorityClass;
}
