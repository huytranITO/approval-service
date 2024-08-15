package com.msb.bpm.approval.appr.model.dto.cms;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.model.entity.CustomerEntity_.CUSTOMER_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY
    , property = CUSTOMER_TYPE, defaultImpl = IndividualCustomerDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = IndividualCustomerDTO.class, name = RB),
    @JsonSubTypes.Type(value = EnterpriseCustomerDTO.class, name = EB)
})
public abstract class CustomerDTO {

  @Size(max = 10)
  private String cifNo;

  @NotBlank
  @Size(max = 36)
  private String refCusId;

  @NotBlank
  @Size(max = 6)
  private String customerType;

  @NotBlank
  @Size(max = 4)
  private String typeOfCustomer;

  @Valid
  @NotNull
  private List<CustomerIdentityDTO> identities;

  @Valid @NotNull private List<CustomerCmsAddressDTO> addresses;

  @Valid
  @NotNull
  private Set<CustomerContactDTO> contacts;

}
