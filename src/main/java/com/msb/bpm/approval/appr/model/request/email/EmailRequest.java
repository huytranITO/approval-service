package com.msb.bpm.approval.appr.model.request.email;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest {

  private List<String> to;

  private List<String> cc;

  private List<String> bcc;

  private String eventCode;

  private Map<String, Object> params;
}