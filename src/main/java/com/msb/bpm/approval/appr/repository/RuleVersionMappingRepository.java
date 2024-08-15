package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.model.entity.RuleVersionMappingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 **/
@Repository
public interface RuleVersionMappingRepository extends JpaRepository<RuleVersionMappingEntity, Long> {

//  Optional<RuleVersionMappingEntity> findByApplicationIdAndRuleCode(Long applicationId, RuleCode ruleCode);
  Optional<List<RuleVersionMappingEntity>> findByApplicationIdAndRuleCode(
      Long applicationId, RuleCode ruleCode);
}
