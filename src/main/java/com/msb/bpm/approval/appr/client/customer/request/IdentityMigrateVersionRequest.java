package com.msb.bpm.approval.appr.client.customer.request;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/11/2023, Monday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SuperBuilder
public class IdentityMigrateVersionRequest implements Serializable {
  private Boolean isPrimary;
  private Long id;
  private String type;
  private String identityNumber;
  private Boolean edit;
  private LocalDate issuedDate;
  private String issuedBy;
  private String issuedPlace;
}
