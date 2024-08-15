package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "application_credit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpCreditId;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "credit_type")
  private String creditType;

  @Basic
  @Column(name = "credit_type_value")
  private String creditTypeValue;

  @Basic
  @Column(name = "guarantee_form")
  private String guaranteeForm;

  @Basic
  @Column(name = "guarantee_form_value")
  private String guaranteeFormValue;

  @Basic
  @Column(name = "document_code")
  private String documentCode;

  @Basic
  @Column(name = "approve_result")
  private String approveResult;

  @Basic
  @Column(name = "approve_result_value")
  private String approveResultValue;

  @Basic
  @Column(name = "loan_amount")
  private BigDecimal loanAmount;

  @Basic
  @Column(name = "currency")
  private String currency;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "is_allocation")
  private Boolean isAllocation;

  @Basic
  @Column(name = "id_draft")
  private String idDraft;

  private transient Long oldId;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinTable(name = "credit_card_mapping",
      joinColumns = {@JoinColumn(name = "application_credit_id", referencedColumnName = "id")},
      inverseJoinColumns = {
          @JoinColumn(name = "application_credit_card_id", referencedColumnName = "id")}
  )
  private ApplicationCreditCardEntity creditCard;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinTable(name = "credit_loan_mapping",
      joinColumns = {@JoinColumn(name = "application_credit_id", referencedColumnName = "id")},
      inverseJoinColumns = {
          @JoinColumn(name = "application_credit_loan_id", referencedColumnName = "id")}
  )
  private ApplicationCreditLoanEntity creditLoan;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinTable(name = "credit_overdraft_mapping",
      joinColumns = {@JoinColumn(name = "application_credit_id", referencedColumnName = "id")},
      inverseJoinColumns = {
          @JoinColumn(name = "application_credit_overdraft_id", referencedColumnName = "id")}
  )
  private ApplicationCreditOverdraftEntity creditOverdraft;
  @Transient
  private List<Long> assets;
}
