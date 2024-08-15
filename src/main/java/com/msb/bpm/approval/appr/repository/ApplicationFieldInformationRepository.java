package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationFieldInformationRepository extends
    JpaRepository<ApplicationFieldInformationEntity, Long> {

  boolean existsById(Long id);

  Optional<ApplicationFieldInformationEntity> findById(Long id);
}
