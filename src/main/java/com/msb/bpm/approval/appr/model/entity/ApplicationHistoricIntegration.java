package com.msb.bpm.approval.appr.model.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "application_historic_transaction")
@Getter
@Setter
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationHistoricIntegration {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private Long id;

  @Basic
  @Column(name = "application_id")
  private Long applicationId;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id", insertable = false, updatable = false)
  private ApplicationEntity application;

  @Basic
  @Column(name = "bpm_id")
  private String bpmId;

  @Basic
  @Column(name = "json_response")
  private String jsonRespone;


  @Basic
  @Column(name = "credit_type")
  private String creditType;

  @Basic
  @Column(name = "card_type")
  private String cardType;

  @Transient
  private String cardProductType;

  @Transient
  private String cardForm;

  @Basic
  @Column(name = "guarantee_form")
  private String guaranteeForm;

  @Basic
  @Column(name = "last_name")
  private String lastName;

  @Basic
  @Column(name = "first_name")
  private String firstName;

  @Basic
  @Column(name = "integrated_system")
  private String integratedSystem;

  @Basic
  @Column(name = "cif")
  private String cif;

  @Basic
  @Column(name = "identifier_code")
  private String identifierCode;

  @Transient
  private String status;

  @Transient
  private String autoDeductRate;

  @Transient
  private String productCode;

  @Transient
  private String deductAccountNumber;

  @Basic
  @Column(name = "application_credit_id")
  private Long applicationCreditId;

  @Basic
  @Column(name = "application_sub_card_credit_id")
  private Long applicationSubCardCreditId;

  @Transient
  private String issuingContract;

  @Transient
  private ApplicationCreditEntity applicationCreditEntity;

  @Basic
  @Column(name = "loan_amount")
  private BigDecimal loanAmount;

  @Basic
  @Column(name = "integrated_status")
  private String integratedStatus;

  @Basic
  @Column(name = "integrated_status_detail")
  private String integratedStatusDetail;

  @Basic
  @Column(name = "error_description")
  private String errorDescription;

  @Basic
  @Column(name = "error_code")
  private String errorCode;

  @Basic
  @Column(name = "next_integrated_time")
  private Date nextIntegratedTime;

  @Column(name = "effective_date")
  private LocalDate effectiveDate;

  @Column(name = "business_unit")
  private String businessUnit;

  @Column(name = "document_code")
  private String documentCode;

  @Transient
  private String fullName;

  @Transient
  private Long applicationCreditCardId;

  @Transient
  private String approveResult;

  @Transient
  private String saleCode;

  @Transient
  private String secretQuestion;

  @Transient
  private String address;

  @Transient
  private String productType;

  @Transient
  private String policyCode;

  @Transient
  private String liabilityType;

  @Transient
  private String branchCode;

  @Transient
  private String businessUnitCode;


  @Transient
  private String regNumber;

  @Transient
  private String businessCode;

  @Transient
  private String type;

  @Transient
  private String channel;

  @Transient
  private int isChargeFee;

  @Transient
  private String email;

  @Transient
  private String institutionCode;

  @CreatedBy
  @Column(name = "created_by")
  private String createdBy;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
