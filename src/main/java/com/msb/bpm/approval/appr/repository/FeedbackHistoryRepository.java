package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackHistoryRepository extends JpaRepository<ApplicationHistoryFeedbackEntity, Long> {
    Optional<List<ApplicationHistoryFeedbackEntity>> findByApplicationBpmId(String bpmId);
}
