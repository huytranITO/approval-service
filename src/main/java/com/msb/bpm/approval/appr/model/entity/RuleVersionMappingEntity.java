package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import lombok.ToString;
import lombok.With;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 **/
@Entity
@Table(name = "rule_version_mapping")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@With
public class RuleVersionMappingEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private Long id;

  @JoinColumn(name = "application_id", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private ApplicationEntity application;

  @Basic
  @Column(name = "rule_code")
  @Enumerated(EnumType.STRING)
  private RuleCode ruleCode;

  @Basic
  @Column(name = "rule_version")
  private Integer ruleVersion;

  @Basic
  @Column(name = "description")
  private String description;

  @Basic
  @Column(name = "group_code")
  private String groupCode;

}
