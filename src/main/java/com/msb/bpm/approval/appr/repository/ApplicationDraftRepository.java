package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ApplicationDraftRepository extends JpaRepository<ApplicationDraftEntity, Long> {

  Optional<Set<ApplicationDraftEntity>> findByBpmId(String bpmId);

  Optional<ApplicationDraftEntity> findByBpmIdAndTabCode(String bpmId, String tabCode);

  Optional<ApplicationDraftEntity> findByBpmIdAndTabCodeAndStatus(String bpmId, String tabCode, Integer status);
}
