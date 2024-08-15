package com.msb.bpm.approval.appr.model.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFieldInformationDTO extends AddressDTO {

  private Long id;

  @Size(max = 10)
  @NotBlank
  private String placeType;

  @Size(max = 100)
  private String placeTypeValue;

  @Size(max = 10)
  @NotBlank
  private String relationship;

  @Size(max = 100)
  private String relationshipValue;

  @NotNull
  private LocalDate timeAt;

  @Size(max = 100)
  @NotBlank
  private String instructor;

  @Size(max = 100)
  @NotBlank
  private String executor;

  @Size(max = 10)
  @NotBlank
  private String result;

  @Size(max = 100)
  @NotBlank
  private String resultValue;

  private Integer orderDisplay;
}
