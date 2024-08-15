package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRelationshipRepository  extends JpaRepository<CustomerRelationShipEntity, Long> {
  void deleteByCustomerRefId(Long cusRefId);
  Set<CustomerRelationShipEntity> findByCustomerRefIdIn(List<Long> ids);

  @Query("select crs from CustomerRelationShipEntity crs join ApplicationEntity a on crs.customer.id = a.customer.id "
      + " and a.bpmId = :bpmId order by crs.customerRefId asc")
  List<CustomerRelationShipEntity> getCustomerRelationByBmpId(@Param("bpmId") String bpmId);


  Optional<Set<CustomerRelationShipEntity>> findByCustomerRefIdIn(Set<Long> ids);

}
