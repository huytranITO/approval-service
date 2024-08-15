package com.msb.bpm.approval.appr.repository;

import com.msb.bpm.approval.appr.model.entity.IDSequenceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 2/11/2023, Thursday
 **/
@Repository
public interface IDSequenceRepository extends JpaRepository<IDSequenceEntity, Long> {

  Optional<IDSequenceEntity> findTopByOrderByIdDesc();
}
