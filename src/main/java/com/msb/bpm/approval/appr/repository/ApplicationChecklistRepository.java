package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationChecklistEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApplicationChecklistRepository extends JpaRepository<ApplicationChecklistEntity, Long> {

  @Transactional(readOnly = true)
  Optional<ApplicationChecklistEntity> findByApplicationIdAndPhase(Long appId, String phase);
}
