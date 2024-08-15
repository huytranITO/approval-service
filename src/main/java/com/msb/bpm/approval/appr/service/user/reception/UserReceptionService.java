package com.msb.bpm.approval.appr.service.user.reception;

import com.msb.bpm.approval.appr.enums.application.AssignType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.dto.authority.UserReceptionDTO;
import java.util.List;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 12/6/2023, Monday
 **/
public interface UserReceptionService {

  List<UserReceptionDTO> getUserByAuthority(String bpmId, String code);

  List<UserReceptionDTO> getReworkUserByApplication(String bpmId);

  List<UserReceptionDTO> getAssignUsers(AssignType assignType);

  List<UserReceptionDTO> getAssignUsersByRole(ProcessingRole processingRole);
}
