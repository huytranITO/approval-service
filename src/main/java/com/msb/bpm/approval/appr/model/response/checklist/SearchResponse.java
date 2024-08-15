package com.msb.bpm.approval.appr.model.response.checklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchResponse {
  private Date timestamp;
  private StatusCode status;
  @JsonProperty("data")
  private Data data;

  @Getter
  @Setter
  @ToString
  public static class Data {
    private List<Content> content;
  }

  @Getter
  @Setter
  @ToString
  public static class Content {
    private Integer id;
    private String code;
    private String fileType;
    private String name;
    private String domainType;
    private Integer maxFileSize;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
  }

  @lombok.Data
  public static class StatusCode {
    private String code;
    private String message;
  }
}
