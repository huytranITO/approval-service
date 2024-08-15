package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.POST_BASE_REQUEST;
import static com.msb.bpm.approval.appr.exception.DomainCode.SAVE_DRAFT_INFO;
import static com.msb.bpm.approval.appr.util.Util.mapTabCode;

import com.msb.bpm.approval.appr.aop.annotation.Validation;
import com.msb.bpm.approval.appr.exception.ValidationRequestException;
import com.msb.bpm.approval.appr.kafka.BeanProducer;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.util.Util;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Configuration
@RequiredArgsConstructor
public class ValidationAspect extends AbstractBaseService {

  private final Validator validator;
  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;

  @Transactional
  @Before(value = "@annotation(com.msb.bpm.approval.appr.aop.annotation.Validation) && args(bpmId,request)",
      argNames = "joinPoint,bpmId,request")
  public void execute(JoinPoint joinPoint, String bpmId, PostBaseRequest request) {
    request.setBpmId(bpmId);
    Set<ConstraintViolation<Object>> violations = validator.validate(request);
    if (CollectionUtils.isNotEmpty(violations)) {
      Validation handle = Util.getAnnotation(joinPoint, Validation.class);
      if (handle.isSaveDraft()) {
        persistApplicationDraft(bpmId, mapTabCode(request.getType()), UNFINISHED, request);

        ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
        metadata.put(APPLICATION_BPM_ID, bpmId);
        metadata.put(POST_BASE_REQUEST, request);
        new BeanProducer(metadata, generalInfoProducerStrategy).execute();

        throw new ValidationRequestException(SAVE_DRAFT_INFO,
            new Object[]{bpmId}, new ConstraintViolationException(violations));
      }
      throw new ConstraintViolationException(violations);
    }
  }
}
