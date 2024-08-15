package com.msb.bpm.approval.appr.service.idgenerate.impl;

import com.msb.bpm.approval.appr.model.entity.IDSequenceEntity;
import com.msb.bpm.approval.appr.repository.IDSequenceRepository;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 2/11/2023, Thursday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class IDSequenceServiceImpl implements IDSequenceService {

  private final IDSequenceRepository idSequenceRepository;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public String generateBpmId() {
    Optional<IDSequenceEntity> idSequenceOptional = idSequenceRepository.findTopByOrderByIdDesc();
    if (idSequenceOptional.isPresent()) {
      IDSequenceEntity idSequenceLatest = idSequenceOptional.get();
      Long nextIndex = idSequenceLatest.getCurrentIndex() + 1;

      IDSequenceEntity idSequenceEntity = new IDSequenceEntity();
      idSequenceEntity.setCurrentIndex(nextIndex);
      idSequenceEntity.setCreatedAt(LocalDateTime.now());
      idSequenceRepository.saveAndFlush(idSequenceEntity);

      String requestCode = StringUtils.leftPad(nextIndex.toString(), 8, '0');
      return String.format(Util.BPM_ID_FORMAT, requestCode);
    }
    return "";
  }
}
