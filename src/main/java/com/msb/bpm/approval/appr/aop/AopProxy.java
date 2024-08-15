package com.msb.bpm.approval.appr.aop;

import com.msb.bpm.approval.appr.aop.annotation.EmailProducer;
import com.msb.bpm.approval.appr.aop.annotation.KafkaProducer;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/5/2023, Wednesday
 **/
@Configuration
@Slf4j
public class AopProxy {

  /**
   * @deprecated since 06/12/2023
   */
  @Deprecated
  @KafkaProducer(topicName = "#topicName", metadata = "#metadata")
  public void sendMessageToKafka(String topicName, ConcurrentMap<String, Object> metadata) {
    log.info("[AopProxy] sendMessageToKafka with topicName={}", topicName);
  }

  @EmailProducer(eventCode = "#eventCode", metadata = "#metadata")
  public void sendEmail(String eventCode, ConcurrentMap<String, Object> metadata) {
    log.info("[AopProxy] sendEmail...");
  }

}
