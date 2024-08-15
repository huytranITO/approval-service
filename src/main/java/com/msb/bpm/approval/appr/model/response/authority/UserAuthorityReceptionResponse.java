package com.msb.bpm.approval.appr.model.response.authority;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/6/2023, Thursday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAuthorityReceptionResponse {

  @JsonProperty("data")
  private List<UserReceptionDTO> userReceptions;
}
