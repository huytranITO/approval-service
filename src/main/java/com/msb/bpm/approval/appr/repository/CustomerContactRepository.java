package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CustomerContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContactEntity, Long> {

}
