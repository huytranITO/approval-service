package com.msb.bpm.approval.appr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;

@Repository
public interface ApplicationCreditConditionRepository extends JpaRepository<ApplicationCreditConditionsEntity, Long> { 
	List<ApplicationCreditConditionsEntity> findByApplicationIdOrderByCreditConditionId(Long applicationId);
}
