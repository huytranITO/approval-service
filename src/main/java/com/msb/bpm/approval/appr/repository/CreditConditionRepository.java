package com.msb.bpm.approval.appr.repository;

import org.springframework.stereotype.Repository;

import com.msb.bpm.approval.appr.model.entity.CreditConditionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreditConditionRepository extends JpaRepository<CreditConditionEntity, Long> {
}