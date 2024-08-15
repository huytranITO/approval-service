package com.msb.bpm.approval.appr.notication;

/*
* @author: BaoNV2
* @since: 16/10/2023 10:37 AM
* @description:
* @update:
*
* */
public interface NotificationInterface {
  void notice(Object... input);

  void alert(Object... input);
}
