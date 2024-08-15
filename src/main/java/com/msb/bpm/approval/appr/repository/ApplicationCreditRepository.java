package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationCreditRepository extends JpaRepository<ApplicationCreditEntity, Long> {
  Set<ApplicationCreditEntity> findByApplicationId(Long id);

  @Query("select count(1) from ApplicationCreditEntity ac join ApplicationEntity a on a.id  = ac.application.id " +
          "where a.bpmId  = :bpmId and ac.guaranteeForm  = :guaForm ")
  Integer countApplicationCreditByBpmId(String bpmId, String guaForm);
}
