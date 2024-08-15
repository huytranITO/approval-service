package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.CicEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CICRepository extends JpaRepository<CicEntity, Long> {
  boolean existsByApplicationIdAndCustomerId(final Long applicationId, final Long customerId);

  Optional<CicEntity> findByApplicationIdAndCustomerId(final Long applicationId, final Long customerId);

  Optional<List<CicEntity>> findByApplicationId(final Long applicationId);

  void deleteAllByCustomerIdIn(List<Long> lstCustomerId);

  List<CicEntity> findByClientQuestionIdIn(Set<Long> clientQuestionIds);

  List<CicEntity> findByClientQuestionId(Long clientQuestionId);

  boolean existsByIdAndIdentifierCode(Long id, String identifierCode);

  List<CicEntity> findAllByClientQuestionIdAndProductCodeAndStatusCode(Long clientQuestionId, String productCode, String statusCode);
}
