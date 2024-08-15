package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.mapper.TotalAssetIncomeMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Entity
@Table(name = "income_evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class IncomeEvaluationEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "total_outstanding_debt")
  private BigDecimal totalOutstandingDebt;

  @Column(name = "estimated_income")
  private BigDecimal estimatedIncome;

  @Column(name = "appraise")
  private String appraise;

  @Column(name = "total_accumulated_asset_value")
  private BigDecimal totalAccumulatedAssetValue;

  @OneToMany(mappedBy = "incomeEvaluation", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<TotalAssetIncomeEntity> totalAssetIncomes = new HashSet<>();


  public void updateTotalAssetIncome(Set<TotalAssetIncomeEntity> totalAssetIncomeUpdate) {
    Map<Long, TotalAssetIncomeEntity> totalAssetIncomeMap = totalAssetIncomeUpdate
        .stream()
        .collect(Collectors.toMap(TotalAssetIncomeEntity::getId, Function.identity()));
    Set<TotalAssetIncomeEntity> totalAssetIncomeRemoveSet = new HashSet<>();
    this.getTotalAssetIncomes().forEach(totalAssetIncome-> {
      if (totalAssetIncomeMap.containsKey(totalAssetIncome.getId())) {
        // Mapping base
        TotalAssetIncomeMapper.INSTANCE.mapToEntity(totalAssetIncome,
            totalAssetIncomeMap.get(totalAssetIncome.getId()));
        totalAssetIncome.setIncomeEvaluation(this);
      } else {
        totalAssetIncomeRemoveSet.add(totalAssetIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(totalAssetIncomeRemoveSet)) {
      removeTotalAssetIncome(totalAssetIncomeRemoveSet);
    }
  }
  private void removeTotalAssetIncome(Set<TotalAssetIncomeEntity> totalAssetIncomeRemoveSet) {
    this.getTotalAssetIncomes().removeAll(totalAssetIncomeRemoveSet);
  }
  public void createTotalAssetIncome(Set<TotalAssetIncomeEntity> totalAssetIncomeCreate) {
    totalAssetIncomeCreate.forEach(totalAssetIncome ->
        totalAssetIncome.setIncomeEvaluation(this));
    this.getTotalAssetIncomes().addAll(totalAssetIncomeCreate);
  }
}
