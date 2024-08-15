package com.msb.bpm.approval.appr.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class MathUtil {
  public static final String MONEY_REGEX = "[0-9]*['.'][0]";
  public static BigDecimal add(BigDecimal a, BigDecimal b) {
    if (a == null && b == null) {
      return null;
    }

    return a == null ? b : (b == null ? a : a.add(b));
  }

  public static String toNumberFormat(String value) {
    try {
      if(StringUtils.isNotEmpty(value) && value.matches(MONEY_REGEX)) {
        String[] tmps = value.split("\\.");
        if(tmps != null) {
          value = tmps[0];
        }
      }
      if (NumberUtils.isCreatable(value)) {
        return NumberFormat.getNumberInstance(Locale.CANADA)
            .format(NumberUtils.createNumber(value));
      }
      return "null";
    } catch (Exception e) {
      log.error("Error: {}", e);
      return "null";
    }
  }
}
