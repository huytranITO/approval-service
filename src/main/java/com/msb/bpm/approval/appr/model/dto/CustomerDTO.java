package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.model.entity.CustomerEntity_.CUSTOMER_TYPE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY
    , property = CUSTOMER_TYPE, defaultImpl = IndividualCustomerDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = IndividualCustomerDTO.class, name = RB),
    @JsonSubTypes.Type(value = EnterpriseCustomerDTO.class, name = EB)
})

@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "relationship",
        fieldDependOns = {"mainCustomer", "customerType"}
    ),
    @CustomValidationFieldDependOn(
        message = "{javax.validation.constraints.NotEmpty.message}",
        field = "identities",
        fieldDependOns = {"customerType"}
    )
})
public abstract class CustomerDTO {

  private Long id;

  private Long refCustomerId;

  @JsonProperty(access = Access.WRITE_ONLY)
  private boolean mainCustomer = false;

  // Require voi CMS
//  @JsonProperty(access = Access.WRITE_ONLY)
  @Size(max = 100)
  private String refCusId;

  @Size(max = 45)
  private String bpmCif;

  @Size(max = 45)
  private String cif;

  @NotBlank
  @Size(max = 6)
  private String customerType;

  @Size(max = 10)
  private String relationship;

  @Size(max = 500)
  private String relationshipValue;

  private Integer orderDisplay;

  private Integer version;

  @Valid
  @JsonProperty(value = "identityDocuments")
  private Set<CustomerIdentityDTO> identities;

  private CustomerIdentityDTO mainIdentity;

  @Valid
  private Set<CustomerAddressDTO> addresses;

  private CustomerAddressDTO mainAddress;

  private Long relationshipRefId;

  private Boolean paymentGuarantee;
  private String issuedBy;
  private LocalDate issuedAt;
  private String documentType;
  private String documentTypeValue;
  private String issuedPlace;
  private String issuedPlaceValue;

  public Set<CustomerIdentityDTO> getIdentities() {
    if (CollectionUtils.isEmpty(identities)) {
      return identities;
    }

    // Sorted by orderDisplay
    return identities.stream()
        .sorted(comparing(CustomerIdentityDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<CustomerAddressDTO> getAddresses() {
    if (CollectionUtils.isEmpty(addresses)) {
      return addresses;
    }

    // Sorted by orderDisplay
    return addresses.stream()
        .sorted(comparing(CustomerAddressDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
