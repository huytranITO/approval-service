package com.msb.bpm.approval.appr.scheduler;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardIntegrationScheduler {

  @Schedules({
      @Scheduled(cron = "${cron.w4.sync.first-time}"),
      @Scheduled(cron = "${cron.w4.sync.second-time}")
  })
  public void execute() {
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    log.info("Task executed at {}", now);
  }

}
