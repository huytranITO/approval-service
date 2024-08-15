package com.msb.bpm.approval.appr.service.intergated.impl;

import com.google.common.collect.Sets;
import com.msb.bpm.approval.appr.client.cas.CASClient;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.cas.PostCASRequest;
import com.msb.bpm.approval.appr.model.response.cas.CASResponse;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRatingsRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.CASService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

@Service
@AllArgsConstructor
@Slf4j
public class CASServiceImpl extends AbstractBaseService implements CASService {
    private final CASClient casClient;
    private final ApplicationCreditRatingsRepository applicationCreditRatingsRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public void getCASInfo(PostCASRequest postCASRequest) {
        ApplicationEntity applicationEntity = applicationRepository.findByBpmId(postCASRequest.getBpmId())
                .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
        // Lưu kết quả application_credit_ratings
        List<ApplicationCreditRatingsEntity> dataSaved = applicationCreditRatingsRepository.findAllByApplicationIdAndRatingIdAndRatingSystem(
                applicationEntity.getId(),
                postCASRequest.getProfileId(),
                RatingSystemType.CAS.name()
        ).orElse(null);
        if (CollectionUtils.isNotEmpty(dataSaved)) {
            // Update rating
            dataSaved.stream()
                    .findFirst().ifPresent(ratingsEntity -> {
                        postCASRequest.setRequestId(ratingsEntity.getRequestId() != null
                                ? ratingsEntity.getRequestId()
                                : UUID.randomUUID().toString());
                        casClient.getCASScore(postCASRequest);
                    });

        } else {
            postCASRequest.setRequestId(UUID.randomUUID().toString());
            // Call api CAS info
            CASResponse casResponse = casClient.getCASScore(postCASRequest);
            if (casResponse != null) {
                // Create rating
                ApplicationCreditRatingsEntity ratingsEntity = new ApplicationCreditRatingsEntity();
                ratingsEntity.setRatingSystem(RatingSystemType.CAS.name());
                ratingsEntity.setRatingId(postCASRequest.getProfileId());
                ratingsEntity.setApplication(applicationEntity);
                ratingsEntity.setRequestId(postCASRequest.getRequestId());
                applicationCreditRatingsRepository.save(ratingsEntity);
            }
        }
    }

    @Override
    @Transactional
    public ApplicationCreditRatingsDTO getCASDetail(PostCASRequest postCASRequest) {

        ApplicationEntity applicationEntity = applicationRepository.findByBpmId(postCASRequest.getBpmId())
                .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
        List<ApplicationCreditRatingsEntity> dataSaved = applicationCreditRatingsRepository.findAllByApplicationIdAndRatingIdAndRatingSystem(
                applicationEntity.getId(),
                postCASRequest.getProfileId(),
                RatingSystemType.CAS.name()
        ).orElse(null);

        if (CollectionUtils.isNotEmpty(dataSaved)) {
            Set<ApplicationCreditRatingsDTO> lstRatingDTO = buildCreditRatings(Sets.newHashSet(dataSaved));
            if (!lstRatingDTO.isEmpty()) {
                return lstRatingDTO.iterator().next();
            }
        }
        return null;
    }

}
