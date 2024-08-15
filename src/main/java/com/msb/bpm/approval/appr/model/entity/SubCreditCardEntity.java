package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Basic;
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
@Table(name = "sub_credit_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class SubCreditCardEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpSubId;

  @ManyToOne
  @JoinColumn(name = "application_credit_card_id", referencedColumnName = "id")
  private ApplicationCreditCardEntity applicationCreditCard;

  @Basic
  @Column(name = "card_owner_name")
  private String cardOwnerName;

  @Basic
  @Column(name = "email")
  private String email;

  @Basic
  @Column(name = "phone_number")
  private String phoneNumber;

  @Basic
  @Column(name = "card_limit_amount")
  private BigDecimal cardLimitAmount;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "contract_number")
  private String contractNumber;

  @Basic
  @Column(name = "created_date")
  private LocalDateTime createdDate;

}
