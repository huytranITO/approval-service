package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationCreditRatingsDtlRepository extends JpaRepository<ApplicationCreditRatingsDtlEntity, Long> {
  Optional<List<ApplicationCreditRatingsDtlEntity>> findAllByApplicationCreditRatingId(Long creditId);
}
