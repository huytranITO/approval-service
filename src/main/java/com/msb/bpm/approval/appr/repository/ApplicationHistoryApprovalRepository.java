package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 16/5/2023, Tuesday
 **/
@Repository
public interface ApplicationHistoryApprovalRepository extends
    JpaRepository<ApplicationHistoryApprovalEntity, Long> {

  @Query("SELECT ah FROM ApplicationHistoryApprovalEntity ah WHERE ah.application.id = :applicationId AND ah.userRole = :processingRole")
  Optional<List<ApplicationHistoryApprovalEntity>> findByApplicationIdAndProcessingRole(
      Long applicationId, ProcessingRole processingRole);

  Optional<List<ApplicationHistoryApprovalEntity>> findByApplicationBpmIdOrderByExecutedAtDesc(
      String bpmId);

  ApplicationHistoryApprovalEntity findFirstByApplicationAndFromUserRoleOrderByExecutedAtDesc(
      ApplicationEntity application,
      ProcessingRole fromUserRole);

  ApplicationHistoryApprovalEntity findFirstByApplicationAndUserRoleOrderByExecutedAtDesc(
      ApplicationEntity application,
      ProcessingRole toUserRole);

  @Query("select distinct ah.application.id from ApplicationHistoryApprovalEntity ah where ah.username = :username and ah.userRole = :userRole")
  List<Long> getApplicationIdList(@Param("username") String username,
      @Param("userRole") ProcessingRole userRole);
}
