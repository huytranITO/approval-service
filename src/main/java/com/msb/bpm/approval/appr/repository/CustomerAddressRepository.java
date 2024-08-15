package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Long> {

}
