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
@Table(name = "saved_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class SavedFileEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private long id;
  @Basic
  @Column(name = "checklist_dtl_id")
  private Long checklistDtlId;
  @Basic
  @Column(name = "file_id")
  private String fileId;
  @Basic
  @Column(name = "file_name")
  private String fileName;
  @Basic
  @Column(name = "file_path")
  private String filePath;
  @Basic
  @Column(name = "file_type")
  private String fileType;
  @Basic
  @Column(name = "is_uploaded")
  private Integer isUploaded;
  @Basic
  @Column(name = "is_deleted")
  private Integer isDeleted;
}
