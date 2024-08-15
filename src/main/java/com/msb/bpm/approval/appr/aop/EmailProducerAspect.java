package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.util.Util.getAnnotation;

import com.msb.bpm.approval.appr.aop.annotation.EmailProducer;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.factory.EmailFactory;
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
import org.springframework.scheduling.annotation.Async;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Aspect
@Configuration
@Slf4j
@RequiredArgsConstructor
public class EmailProducerAspect {
  private final CustomSpringExpressionLanguageParser languageParser;
  private final Executor executor;
  private final EmailFactory emailFactory;

  @Around("@annotation(com.msb.bpm.approval.appr.aop.annotation.EmailProducer)")
  public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
    Object entity = joinPoint.proceed();

    EmailProducer emailProducer = getAnnotation(joinPoint, EmailProducer.class);
    boolean isPublish = emailProducer.isPublish();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String eventCode = (String) languageParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), emailProducer.eventCode());
    ConcurrentMap<String, Object> metadata = (ConcurrentHashMap<String, Object>) languageParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), emailProducer.metadata());
    CompletableFuture.runAsync(() -> publishEmail(isPublish, eventCode, metadata), executor);
    return entity;
  }

  @Async
  public void publishEmail(boolean isPublish, String eventCode,
      ConcurrentMap<String, Object> metadata) {
    log.info("Email producer is {}", !isPublish ? "disabled" : "enabled");
    if (!isPublish) {
      return;
    }
    EventCodeEmailType eventCodeEmailType = EventCodeEmailType.valueOf(eventCode);
    log.info("START publish email with eventCode={}", eventCodeEmailType);
    emailFactory.getSenderStrategy(eventCodeEmailType).setMetadata(metadata);
    emailFactory.getSenderStrategy(eventCodeEmailType).publishEmail(eventCode);
    log.info("END publish email with eventCode={}", eventCodeEmailType);
  }
}
