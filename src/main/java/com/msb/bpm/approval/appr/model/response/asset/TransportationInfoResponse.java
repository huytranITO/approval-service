package com.msb.bpm.approval.appr.model.response.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/9/2023, Wednesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportationInfoResponse implements Serializable {

  private Long id;

  private String address;

  private String seriNumber;

  private String machineNumber;

  private String plateNumber;

  private String description;

  private boolean futureAsset;

  private String propertyDetails;
}
