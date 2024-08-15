package com.msb.bpm.approval.appr.mqtt.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

  private ContentType type;

  private ContentDetail detail;

}
