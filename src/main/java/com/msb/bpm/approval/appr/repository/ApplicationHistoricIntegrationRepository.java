package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationHistoricIntegrationRepository extends JpaRepository<ApplicationHistoricIntegration, Long>,
    JpaSpecificationExecutor<ApplicationHistoricIntegration> {

  Optional<List<ApplicationHistoricIntegration>> findByBpmId(String bpmId);

  Optional<List<ApplicationHistoricIntegration>> findByBpmIdAndIntegratedSystem(String bpmId, String integratedSystem);

  @Query("SELECT a FROM ApplicationHistoricIntegration a WHERE a.integratedSystem = :system AND a.effectiveDate >= :time AND (a.integratedStatus LIKE '%Lỗi%' OR a.integratedStatus LIKE '%Chờ%')")
  List<ApplicationHistoricIntegration> findByErrorStatus(String system, LocalDate time);

  @Query("SELECT a FROM ApplicationHistoricIntegration a WHERE a.integratedSystem = :status AND a.id = :id AND a.effectiveDate >= :time")
  ApplicationHistoricIntegration findInternal(String status, Long id, LocalDate time);

  List<ApplicationHistoricIntegration> findByApplicationIdAndBpmId(Long applicationId, String bpmId);

}

