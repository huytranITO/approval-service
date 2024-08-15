package com.msb.bpm.approval.appr.model.response.esb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListOfCAAccountsResponse {

  @JsonProperty("CAAccounts")
  private List<AccountResponse> caAccounts = new ArrayList<>();
}
