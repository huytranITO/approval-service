package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.model.dto.AddressDTO;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@SuperBuilder
public class FormTemplateApplicationFieldInformationDTO extends AddressDTO {

  private Long id;

  private String placeType = "null";

  private String placeTypeValue = "null";

  private String relationship = "null";

  private String relationshipValue = "null";

  @NotNull
  private LocalDate timeAt;

  private String instructor = "null";

  private String executor = "null";

  private String result = "null";

  private String resultValue = "null";

  private Integer orderDisplay;
}
