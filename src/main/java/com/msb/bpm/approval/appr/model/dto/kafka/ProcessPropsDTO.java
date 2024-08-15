package com.msb.bpm.approval.appr.model.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPropsDTO {
    String authority;
    String detail;
    List<ReasonDTO> reasons;
}
