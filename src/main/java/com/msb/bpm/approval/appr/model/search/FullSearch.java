package com.msb.bpm.approval.appr.model.search;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 18/10/2023, Wednesday
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class FullSearch extends BaseSearch implements Serializable {

  private boolean firstTime;
  private String fullText;
}
