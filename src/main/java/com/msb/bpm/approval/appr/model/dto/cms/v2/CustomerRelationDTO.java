package com.msb.bpm.approval.appr.model.dto.cms.v2;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.model.entity.CustomerEntity_.CUSTOMER_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import com.msb.bpm.approval.appr.validator.CustomerTypeConstraint;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY
    , property = CUSTOMER_TYPE, defaultImpl = CmsCustomerRelationDTO.class)

@JsonSubTypes({
    @JsonSubTypes.Type(value = CmsCustomerRelationDTO.class, name = RB),
})
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "staffId",
        fieldDependOns = "msbMember"
    )
})
public abstract class CustomerRelationDTO implements Serializable {

  @Size(max = 45)
  private String cifNo;

  @NotBlank
  @Size(max = 100)
  private String refCusId;

  @NotBlank
  @Size(max = 6)
  @CustomerTypeConstraint
  private String customerType;

  @NotBlank
  @Size(max = 4)
  @CategoryConstraint(category = ConfigurationCategory.TYPE_OF_CUSTOMER)
  private String typeOfCustomer;

  @Valid
  @NotNull
  @NotEmpty
  private List<CmsCustomerIdentityDTO> identities;

  @Valid
  @NotNull
  @NotEmpty
  private List<CmsCustomerAddressDTO> addresses;

//  @Valid
//  @NotNull
  private Set<CmsCustomerContactDTO> contacts;

  private boolean msbMember;

  @Size(max = 20)
  private String staffId;
}
