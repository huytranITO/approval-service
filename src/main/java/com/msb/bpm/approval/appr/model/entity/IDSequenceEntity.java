package com.msb.bpm.approval.appr.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 2/11/2023, Thursday
 **/
@Entity
@Table(name = "id_sequence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDSequenceEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long currentIndex;

  private LocalDateTime createdAt;
}
