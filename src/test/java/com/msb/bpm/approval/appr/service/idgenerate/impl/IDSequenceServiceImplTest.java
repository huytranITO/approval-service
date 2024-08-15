package com.msb.bpm.approval.appr.service.idgenerate.impl;

import com.msb.bpm.approval.appr.model.entity.IDSequenceEntity;
import com.msb.bpm.approval.appr.repository.IDSequenceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class IDSequenceServiceImplTest {
    @InjectMocks
    private IDSequenceServiceImpl idSequenceService;
    @Mock
    private IDSequenceRepository idSequenceRepository;

    @Test
    void should_generateBpmId_test() {
        Optional<IDSequenceEntity> idSequenceOptional = Optional.of(new IDSequenceEntity());
        Mockito.when(idSequenceRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        Assertions.assertEquals(idSequenceService.generateBpmId(), "");
        idSequenceOptional.get().setId(Long.valueOf(1));
        idSequenceOptional.get().setCurrentIndex(Long.valueOf(1));
        idSequenceOptional.get().setCreatedAt(LocalDateTime.now());
        Mockito.when(idSequenceRepository.findTopByOrderByIdDesc()).thenReturn(idSequenceOptional);
        Assertions.assertEquals(idSequenceService.generateBpmId(), "151-00000002");
    }
}
