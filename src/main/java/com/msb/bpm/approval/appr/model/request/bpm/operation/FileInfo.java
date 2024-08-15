package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileInfo {
  private String path;
  private String name;
  private String type;
}
