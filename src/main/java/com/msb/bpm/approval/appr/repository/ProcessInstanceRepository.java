package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstanceEntity, Long> {

  Optional<ProcessInstanceEntity> findByProcessInstanceId(String processInstanceId);

  Optional<ProcessInstanceEntity> findByProcessBusinessKey(String processBusinessKey);

  @Query("SELECT a.processInstance FROM ApplicationEntity a WHERE a.bpmId = :bpmId")
  Optional<ProcessInstanceEntity> findByBpmId(String bpmId);
}
