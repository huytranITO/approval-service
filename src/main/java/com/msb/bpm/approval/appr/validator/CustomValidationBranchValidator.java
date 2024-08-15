package com.msb.bpm.approval.appr.validator;

import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest.Filter;
import com.msb.bpm.approval.appr.model.response.usermanager.GetOrganizationResponse;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@Slf4j
public class CustomValidationBranchValidator implements ConstraintValidator<BranchConstraint, String> {
  private ConfigurationCategory category;
  private final UserManagerClient userManagerClient;

  @Override
  public void initialize(BranchConstraint branchAnnotation) {
    ConstraintValidator.super.initialize(branchAnnotation);
    category = branchAnnotation.category();
  }

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isBlank(s)) {
      return true;
    }

    Filter filter = Filter.builder()
        .page(0)
        .pageSize(1000)
        .build();

    GetOrganizationRequest request = GetOrganizationRequest.builder()
        .specializedBank("RB")
        .name("MSB")
        .type("DVKD")
        .filter(filter)
        .build();

    GetOrganizationResponse response = userManagerClient.findOrganization(request);
    return response == null || response.getData() == null ||
        response.getData().getOrganizations().stream().filter(item -> !item.getCode().contains("DS")
            && !item.getCode().contains("LH")
            && !item.getCode().contains("T")
            && !item.getCode().contains("L")
            && !item.getCode().contains("SSE"))
            .anyMatch(matching -> matching.getCode().equals(s));
  }
}
