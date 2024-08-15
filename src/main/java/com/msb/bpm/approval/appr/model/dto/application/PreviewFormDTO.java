package com.msb.bpm.approval.appr.model.dto.application;

import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistFileMessageDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/6/2023, Tuesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PreviewFormDTO {

  private String generatorStatus;

  private List<ChecklistFileMessageDTO> files = new ArrayList<>();

  private PopupControlDTO popupControl;

}
