package com.msb.bpm.approval.appr.service.authority.reception;

import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import java.util.List;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/6/2023, Monday
 **/
public interface AuthorityReceptionService {

  List<AuthorityDetailDTO> getAuthoritiesByApplication(String bpmId);
}
