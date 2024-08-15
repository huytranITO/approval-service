package com.msb.bpm.approval.appr.controller.test;

import com.msb.bpm.approval.appr.mqtt.MQTTService;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.notication.CICPDFNotificationStrategy;
import com.msb.bpm.approval.appr.notication.CICRatioNotificationStrategy;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.BpmOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestIntegratedController {

    private final BpmOperationService bpmOperationService;
    private final ApiRespFactory apiRespFactory;
    private final ApplicationRepository applicationRepository;
    private final MQTTService mqttService;
    private final CICPDFNotificationStrategy cicpdfNotificationStrategy;
    private final CICRatioNotificationStrategy cicRatioNotificationStrategy;

    @RequestMapping("/api/v1/test/integrated/bpm/{bpmId}")
    @Transactional
    public ResponseEntity<ApiResponse> testIntegrateBpmOperation(@PathVariable String bpmId) {
        ApplicationEntity app = applicationRepository.findByBpmId(bpmId)
                .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
        bpmOperationService.syncBpmOperation(app, false);
        return apiRespFactory.success();
    }

    @GetMapping("/test/integrated/mqtt/cic")
    public ResponseEntity<ApiResponse> mqttCic(
        @RequestParam String userName,
        @RequestParam String applicationId) {
        mqttService.sendCICMessage(userName, applicationId);
        cicpdfNotificationStrategy.notice(applicationId, userName);
        cicRatioNotificationStrategy.notice(applicationId, userName);
        return apiRespFactory.success();
    }
}
