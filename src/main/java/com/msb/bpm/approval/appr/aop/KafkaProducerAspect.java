package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_APPROVAL_RB_INFO_KEY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_FEEDBACK_APPLICATION_CMS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.GENERAL_INFO_KEY;
import static com.msb.bpm.approval.appr.util.Util.getAnnotation;

import com.msb.bpm.approval.appr.aop.annotation.KafkaProducer;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.kafka.Producer;
import com.msb.bpm.approval.appr.kafka.producer.CmsInfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsV2InfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 4/5/2023, Thursday
 * @deprecated since 06/12/2023
 **/
@Aspect
@Configuration
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class KafkaProducerAspect {

  private final CustomSpringExpressionLanguageParser languageParser;
  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;
  private final ApplicationConfig applicationConfig;
  private final CmsInfoProducerStrategy cmsInfoProducerStrategy;
  private final CmsV2InfoProducerStrategy cmsV2InfoProducerStrategy;
  private final Executor executor;

  @Order(3)
  @Around("@annotation(com.msb.bpm.approval.appr.aop.annotation.KafkaProducer)")
  public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
    Object entity = joinPoint.proceed();

    KafkaProducer kafkaProducer = getAnnotation(joinPoint, KafkaProducer.class);
    boolean isPublish = kafkaProducer.isPublish();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String topicName = (String) languageParser.getDynamicValue(signature.getParameterNames(),
        joinPoint.getArgs(), kafkaProducer.topicName());
    ConcurrentMap<String, Object> metadata = (ConcurrentHashMap<String, Object>) languageParser.getDynamicValue(
        signature.getParameterNames(), joinPoint.getArgs(), kafkaProducer.metadata());

    log.info("Kafka producer is {}", !isPublish ? "disabled" : "enabled");

    if (!isPublish) {
      return entity;
    }

    CompletableFuture.runAsync(() -> {
      if (applicationConfig.getKafka().getTopic().get(GENERAL_INFO_KEY)
          .getTopicName().equalsIgnoreCase(topicName)) {
        generalInfoProducerStrategy.setMetadata(metadata);
        new Producer(topicName).produce(generalInfoProducerStrategy);
      }

      if (applicationConfig.getKafka().getTopic().get(BPM_APPROVAL_RB_INFO_KEY)
          .getTopicName().equalsIgnoreCase(topicName)) {
        cmsInfoProducerStrategy.setMetadata(metadata);
        new Producer(topicName).produce(cmsInfoProducerStrategy);
      }

      if (applicationConfig.getKafka().getTopic().get(BPM_FEEDBACK_APPLICATION_CMS)
          .getTopicName().equalsIgnoreCase(topicName)) {
        cmsV2InfoProducerStrategy.setMetadata(metadata);
        new Producer(topicName).produce(cmsV2InfoProducerStrategy);
      }
    }, executor);

    return entity;
  }

}
