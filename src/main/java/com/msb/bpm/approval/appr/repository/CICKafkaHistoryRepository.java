package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CICKafkaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CICKafkaHistoryRepository extends JpaRepository<CICKafkaHistory, Long> {

  CICKafkaHistory findByRequestId(String requestId);

}
