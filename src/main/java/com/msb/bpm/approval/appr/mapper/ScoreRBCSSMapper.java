package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.css.ScoreRBCSS;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBResponseData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Mapper
public interface ScoreRBCSSMapper {

  ScoreRBCSSMapper INSTANCE = Mappers.getMapper(ScoreRBCSSMapper.class);

  @Mapping(source = "scoreRM", target = "scoresRM", qualifiedByName = "stringToBigDecimalList")
  @Mapping(source = "approvalScore", target = "approvalScores", qualifiedByName = "stringToBigDecimalList")
  ScoreRBCSS sourceToDestination(GetScoreRBResponseData getScoreRBResponseData);

  @Named("stringToBigDecimalList")
  static List<BigDecimal> parseStringToBigDecimalList(List<String> stringList) {
    if (CollectionUtils.isEmpty(stringList)) {
      return new ArrayList<>();
    }
    return stringList.stream()
        .filter(s -> StringUtils.hasText(s))
        .map(s -> {
          try {
            return new BigDecimal(s);
          } catch (Exception ex) {
            return null;
          }
        }).filter(bigDecimal -> bigDecimal != null)
        .collect(Collectors.toList());
  }
}
