package com.msb.bpm.approval.appr.model.response.esb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class ListOfSAAccountsResponse {

  @JsonProperty("SAAccounts")
  private List<AccountResponse> saAccounts = new ArrayList<>();
}
