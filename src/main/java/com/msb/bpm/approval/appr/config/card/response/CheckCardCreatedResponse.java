package com.msb.bpm.approval.appr.config.card.response;


import java.util.ArrayList;
import java.util.Date;
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
public class CheckCardCreatedResponse {
  private Date timestamp;
  private Integer code;
  private String message;
  private Data data;

  @Getter
  @Setter
  public static class Data {
    private String liabilityContract;
    private List<IssueContract> listIssueContract = new ArrayList<>();
    @Getter
    @Setter
    public static class IssueContract {
      private String issueContract;
      private List<CardInfo> mainCard = new ArrayList<>();
      private List<CardInfo> subCard = new ArrayList<>();
      @Getter
      @Setter
      public static class CardInfo {
        private String contractNumber;
        private String contractName;
        private Date dateOpen;
        private String statusName;
        private Integer contrStatus;
      }
    }
  }

}
