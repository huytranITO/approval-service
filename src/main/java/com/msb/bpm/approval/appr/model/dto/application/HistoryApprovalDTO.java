package com.msb.bpm.approval.appr.model.dto.application;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 4/6/2023, Sunday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistoryApprovalDTO {
  private Long applicationId;
  private String userName;
  private String userRole;
  private String fromUserRole;
  private String step;
  private Set<String> reasons;
  private String proposalApprovalReception;
  private String proposalApprovalUser;
  private String note;
  private String status;
}
