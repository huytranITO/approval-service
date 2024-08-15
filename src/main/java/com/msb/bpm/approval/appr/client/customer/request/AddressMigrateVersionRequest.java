package com.msb.bpm.approval.appr.client.customer.request;

import java.io.Serializable;
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
public class AddressMigrateVersionRequest implements Serializable {
  private Long id;
  private String addressType;
  private String cityCode;
  private boolean edit;
  private String districtCode;
  private String wardCode;
  private String addressLine;
  private Boolean hktt;
}
