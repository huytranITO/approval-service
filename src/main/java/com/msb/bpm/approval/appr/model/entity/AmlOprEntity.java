package com.msb.bpm.approval.appr.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table
@Entity(name = "aml_opr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class AmlOprEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "ref_customer_id")
  private Long refCustomerId;

  @Column(name = "query_type")
  private String queryType;

  @Column(name = "result_code")
  private String resultCode;

  @Column(name = "result_description")
  private String resultDescription;

  @Column(name = "reason_in_list")
  private String resultOnBlackList;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "subject")
  private String subject;

  @Basic
  @Column(name = "identifier_code")
  private String identifierCode;

  @Column(name = "start_date")
  private String startDate;

  @Column(name = "end_date")
  private String endDate;

  @Column(name = "order_display")
  private Integer orderDisplay;

  private boolean priority;

  @Column(name = "asset_group")
  private String assetGroup;

  @Column(name = "asset_type")
  private String assetType;

  @Column(name = "sync_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime syncDate;
}
