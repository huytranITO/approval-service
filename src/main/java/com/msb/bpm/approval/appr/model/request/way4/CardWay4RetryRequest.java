package com.msb.bpm.approval.appr.model.request.way4;


import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardWay4RetryRequest {
  private List<CardRequest> cardList;
  @Getter
  @Setter
  public static final class CardRequest {
    @NotNull
    private Long id;
  }

}
