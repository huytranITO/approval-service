package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsRentalIncomeDTO extends CmsBaseIncomeItemDTO implements Serializable {

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.ASSET_TYPE)
  private String assetType;

  @Size(max = 255)
  private String assetOwner;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.RENTAL_PURPOSE)
  private String rentalPurpose;

  @Size(max = 255)
  private String assetAddress;

  @Size(max = 255)
  private String renter;

  @Size(max = 20)
  private String renterPhone;

  @Digits(integer = 15, fraction = 0)
  private BigDecimal rentalPrice;
}
