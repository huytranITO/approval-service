package com.msb.bpm.approval.appr.model.entity;

import java.sql.Date;
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
@Table(name = "checklist_dtl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ChecklistDtlEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private long id;
  @Basic
  @Column(name = "areas_id")
  private Long areasId;
  @Basic
  @Column(name = "code")
  private String code;
  @Basic
  @Column(name = "name")
  private String name;
  @Basic
  @Column(name = "props")
  private byte[] props;
  @Basic
  @Column(name = "return_code")
  private String returnCode;
  @Basic
  @Column(name = "return_date")
  private Date returnDate;
  @Basic
  @Column(name = "is_required")
  private Integer isRequired;
  @Basic
  @Column(name = "is_error")
  private Integer isError;
  @Basic
  @Column(name = "is_generated")
  private Integer isGenerated;
  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
  @Basic
  @Column(name = "file_type")
  private String fileType;
  @Basic
  @Column(name = "max_file_size")
  private Integer maxFileSize;
  @Basic
  @Column(name = "is_deleted")
  private Integer isDeleted;
}
