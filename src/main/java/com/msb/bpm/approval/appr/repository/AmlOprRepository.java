package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmlOprRepository extends JpaRepository<AmlOprEntity, Long> {
  boolean existsByApplicationIdAndCustomerIdAndQueryType(final Long applicationId, final Long customerId,
                                                         final String queryType);

  Optional<AmlOprEntity> findByApplicationIdAndCustomerIdAndQueryType(final Long applicationId, final Long customerId,
                                                          final String queryType);

  Optional<List<AmlOprEntity>> findByApplicationId(final Long applicationId);

  void deleteAllByCustomerIdIn(List<Long> lstCustomerId);

  boolean existsByIdAndIdentifierCode(Long id, String identifierCode);

  AmlOprEntity findByApplicationIdAndIdentifierCodeAndAssetGroupAndAssetType(Long applicationId, String identifierCode, String assetGroup, String assetType);

  Optional<List<AmlOprEntity>> findByApplicationIdAndQueryType(Long applicationId, String queryType);
}
