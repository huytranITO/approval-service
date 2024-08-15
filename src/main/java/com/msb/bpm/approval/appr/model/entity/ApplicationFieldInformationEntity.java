package com.msb.bpm.approval.appr.model.entity;

import java.time.LocalDate;
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
@Table(name = "application_field_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationFieldInformationEntity extends AddressEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "place_type")
  private String placeType;

  @Basic
  @Column(name = "place_type_value")
  private String placeTypeValue;

  @Basic
  @Column(name = "relationship")
  private String relationship;

  @Basic
  @Column(name = "relationship_value")
  private String relationshipValue;

  @Basic
  @Column(name = "time_at")
  private LocalDate timeAt;

  @Basic
  private String instructor;

  @Basic
  private String executor;

  @Basic
  private String result;

  @Basic
  @Column(name = "result_value")
  private String resultValue;

  @Basic
  @Column(name = "address_link_id")
  private String addressLinkId;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}
