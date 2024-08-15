package com.msb.bpm.approval.appr.notication;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
* @author: BaoNV2
* @since: 16/10/2023 10:37 AM
* @description:
* @update:
*
* */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeMessage {
  private String notificationType;
  private String bizType;
  private String module;
  private String from;
  private String recipientType;
  private List<String> to;
  private String title;
  private String content;
  private String link;
}
