package com.msb.bpm.approval.appr.model.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class AreasEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private long id;
  @Basic
  @Column(name = "code")
  private String code;
  @Basic
  @Column(name = "name")
  private String name;
  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
  @Basic
  @Column(name = "parent_code")
  private String parentCode;
  @Basic
  @Column(name = "parent_name")
  private String parentName;
  @Basic
  @Column(name = "parent_order_display")
  private Integer parentOrderDisplay;
  @Basic
  @Column(name = "request_code")
  private String requestCode;
  @Basic
  @Column(name = "is_deleted")
  private Integer isDeleted;
}
