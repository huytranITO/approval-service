package com.msb.bpm.approval.appr.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/5/2023, Monday
 **/
@Getter
@AllArgsConstructor
public enum ApprovalResult {

  Y("Đồng ý"), N("Từ chối");

  private final String value;

}
