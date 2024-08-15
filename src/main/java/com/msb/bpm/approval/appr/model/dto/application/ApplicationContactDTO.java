package com.msb.bpm.approval.appr.model.dto.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/8/2023, Tuesday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationContactDTO implements Serializable {

  @Size(max = 10)
  private String relationship;

  @Size(max = 100)
  private String relationshipTxt;

  @Size(max = 100)
  private String fullName;

  @Size(max = 15)
  private String phoneNumber;

  private Integer orderDisplay;

}
