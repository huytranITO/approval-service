package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerIdentityRepository extends JpaRepository<CustomerIdentityEntity, Long> {

}
