package com.msb.bpm.approval.appr.service.applicationListenerEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.mqtt.MQTTService;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.applicationListenerEvent.events.SentCompleteGenerateFormTemplateEvent;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class ApplicationListenerEvent extends AbstractBaseService {

    private final MQTTService mqttService;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    public void SentCompleteGenerateFormTemplateEvent(SentCompleteGenerateFormTemplateEvent sentCompleteGenerateFormTemplateEvent) {
        log.info("SentCompleteGenerateFormTemplateEvent with param: {}", JsonUtil.convertObject2String(sentCompleteGenerateFormTemplateEvent, objectMapper));
        mqttService.sendGenerateFormTemplateMessage(sentCompleteGenerateFormTemplateEvent.getUserName(), sentCompleteGenerateFormTemplateEvent.getApplicationId());;
    }

}
