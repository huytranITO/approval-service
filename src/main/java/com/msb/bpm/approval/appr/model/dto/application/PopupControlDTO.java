package com.msb.bpm.approval.appr.model.dto.application;

import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/6/2023, Tuesday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupControlDTO {

  private boolean isAcceptApproval;

  private boolean isShowAuthorityReception;

  private boolean isShowUserReception;

  private AuthorityDetailDTO authorityReception;

  private UserReceptionDTO userReception;
}
