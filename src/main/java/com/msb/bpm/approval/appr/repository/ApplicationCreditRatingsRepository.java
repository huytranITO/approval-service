package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationCreditRatingsRepository extends JpaRepository<ApplicationCreditRatingsEntity, Long> {
  Optional<List<ApplicationCreditRatingsEntity>> findAllByApplicationIdAndRatingIdAndRatingSystem(Long applicationId, String ratingId, String system);

  Optional<ApplicationCreditRatingsEntity> findByRequestId(String requestId);
}
