package com.msb.bpm.approval.appr.model.entity;

import lombok.*;

import javax.persistence.*;

@Table
@Entity(name = "ap_param")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApParamEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "code")
  private String code;

  @Column(name = "message")
  private String message;

  @Column(name = "type")
  private String type;

}
