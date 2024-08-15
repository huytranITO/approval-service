package com.msb.bpm.approval.appr.model.dto.cbt;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = { "idSrc" })
public class CustomerDataDTO {
  private Long id;
  private Long idSrc;
  private Integer version;
  private List<Long> relationIds;
}
