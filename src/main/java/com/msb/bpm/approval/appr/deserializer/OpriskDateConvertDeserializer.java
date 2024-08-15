package com.msb.bpm.approval.appr.deserializer;

import static com.msb.bpm.approval.appr.constant.Constant.DD_M_YYYY_FORMAT;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/7/2023, Monday
 **/
public class OpriskDateConvertDeserializer extends StdConverter<String, LocalDate> {

  @Override
  public LocalDate convert(String s) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_M_YYYY_FORMAT);
    if (StringUtils.isNoneBlank(s)) {
      return LocalDate.parse(s, formatter);
    }
    return null;
  }
}
