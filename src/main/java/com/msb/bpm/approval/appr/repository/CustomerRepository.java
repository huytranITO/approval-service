package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findById(Long id);

  Optional<CustomerEntity> findByRefCusId(String refCusId);

  Optional<Set<CustomerEntity>> findByIdIn(Set<Long> ids);
  @Query("select distinct ce.refCustomerId from CustomerEntity ce where ce.bpmCif in :bpmCifs")
  Optional<List<Long>> searchCustomerIdsByBpmCifs(List<String> bpmCifs);

  boolean existsById(final Long id);

  Optional<CustomerEntity> findFirstByRefCustomerIdOrderByCreatedAtDesc(Long refCustomerId);

  @Query("select ce from CustomerEntity ce where ce.id in "
      + "(select crs.customerRefId from CustomerRelationShipEntity crs join ApplicationEntity a on crs.customer.id = a.customer.id "
      + "and a.bpmId = :bpmId) and ce.customerType = :customerType order by ce.id asc")
  List<CustomerEntity> getAllCustomerRelation(@Param("bpmId") String bpmId, @Param("customerType") String customerType );

  @Query("select ce from CustomerEntity ce where ce.id in "
      + "(select crs.customerRefId from CustomerRelationShipEntity crs join ApplicationEntity a on crs.customer.id = a.customer.id "
      + "and a.bpmId = :bpmId) order by ce.id asc")
  List<CustomerEntity> getAllCustomerRelationByBpmId(@Param("bpmId") String bpmId);

  @Query("SELECT c FROM CustomerEntity c WHERE c.version IS NULL AND (:customerId IS NULL OR c.refCustomerId = :customerId) AND c.customerType = :customerType")
  Optional<List<CustomerEntity>> findAllByVersionIsNull(Long customerId, String customerType);
  @Query("select ce.refCustomerId from CustomerEntity ce where ce.id = :customerId")
  Optional<Long> searchRefCustomerIdById(Long customerId);
}
