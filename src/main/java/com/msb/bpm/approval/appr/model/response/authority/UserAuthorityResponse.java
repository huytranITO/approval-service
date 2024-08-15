package com.msb.bpm.approval.appr.model.response.authority;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorityResponse {
  private List<AuthorityInfoResponse> data = new ArrayList<>();
}
