package com.msb.bpm.approval.appr.model.dto.feedback;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentFileCommentFbDTO implements Serializable {

  private String docCode;
  private String docName;
  private String group;
  private String checkEnough;
  private List<String> fileName;
}
