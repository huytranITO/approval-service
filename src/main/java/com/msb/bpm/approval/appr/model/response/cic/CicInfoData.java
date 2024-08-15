package com.msb.bpm.approval.appr.model.response.cic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CicInfoData {
    private Map<String, String> errorCodeSBV;
    private String requestTime;
    private String clientQuestionId;
    private String content;
    private String status;
    private String h2hResponseTime;
}
