package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "total_asset_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class TotalAssetIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "asset_group")
  private String assetGroup;

  @Column(name = "asset_group_value")
  private String assetGroupValue;

  @Column(name = "base_document")
  private String baseDocument;

  @Column(name = "asset_description")
  private String assetDescription;

  @Column(name = "asset_value")
  private BigDecimal assetValue;

  @Column(name = "evaluate_method")
  private String evaluateMethod;

  @Column(name = "evaluate_method_value")
  private String evaluateMethodValue;

  @Column(name = "order_display")
  private Integer orderDisplay;

  @ManyToOne
  @JoinColumn(name = "income_evaluation_id", referencedColumnName = "id")
  private IncomeEvaluationEntity incomeEvaluation;
}
