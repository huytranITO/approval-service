package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/9/2023, Friday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsSubCardDTO implements Serializable {

  @Size(max = 36)
  private String subCardId;

  @Size(max = 255)
  private String subCardName;

  @Size(max = 50)
  @Email
  private String subCardEmail;

  @Size(max = 20)
  @JsonProperty("subCardphone")
  private String subCardPhone;

  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 14, fraction = 0)
  private BigDecimal subCardLimit;
}
