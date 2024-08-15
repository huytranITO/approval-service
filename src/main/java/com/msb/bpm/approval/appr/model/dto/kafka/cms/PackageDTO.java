package com.msb.bpm.approval.appr.model.dto.kafka.cms;

import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredLoanDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredOverdraftDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {

    List<UnsecuredCreditCardDTO> unsecuredCreditCard;
    List<UnsecuredLoanDTO> unsecuredLoan;
    List<UnsecuredOverdraftDTO> unsecuredOverdraft;
}
