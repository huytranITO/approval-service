package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApParamRepository extends JpaRepository<ApParamEntity, Long> {
  Optional<ApParamEntity> findByCodeAndType(String code, String type);
  List<ApParamEntity> findAllByType(String type);
}
