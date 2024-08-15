package com.msb.bpm.approval.appr.model.entity;

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

@With
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cic_kafka_history")
public class CICKafkaHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "topic")
  private String topic;

  @Column(name = "message")
  private String message;

  @Column(name = "application_bpm_id")
  private String applicationBpmId;

  @Column(name = "application_id")
  private Long applicationId;

  @Column(name = "client_question_ids")
  private String clientQuestionIds;
}
