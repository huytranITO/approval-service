package com.msb.bpm.approval.appr.model.response.advancedFunctionality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationPushKafkaResponse {
    private String bpmId;
    private String status;
    private String assignee;
    private String processingRole;
    private String previousRole;
    private String processingStepCode;
    private String processingStep;
}
