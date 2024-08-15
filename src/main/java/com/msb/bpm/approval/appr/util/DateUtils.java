package com.msb.bpm.approval.appr.util;


import liquibase.repackaged.org.apache.commons.lang3.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@Slf4j
public class DateUtils {

  public static LocalDateTime getByPeriodTime(LocalDateTime currentDate, Integer periodTime, String type) {
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(toDate(currentDate));
      if (periodTime != null && type != null) {
        switch (type) {
          case "Ngày":
            calendar.add(Calendar.DATE, periodTime);
            break;
          case "Tháng":
            calendar.add(Calendar.MONTH, periodTime);
            break;
          case "Năm":
            calendar.add(Calendar.YEAR, periodTime);
            break;
          default:
            break;
        }
      }
      Instant instant = calendar.getTime().toInstant();
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  public static Date toDate(LocalDateTime localDateTime)
  {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }


  static public LocalDateTime toLdt(Date date) {
    if (date == null) return null;
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);
    ZonedDateTime zdt = cal.toZonedDateTime();
    return zdt.toLocalDateTime();
  }

  public static String format(LocalDateTime time, String format) {
    if (time == null) {
      return StringUtil.EMPTY;
    }
    return DateTimeFormatter.ofPattern(format).format(time);
  }

  public static String format(LocalDate time, String format) {
    if (time == null) {
      return StringUtil.EMPTY;
    }
    return DateTimeFormatter.ofPattern(format).format(time);
  }
  public static String format(Date time, String format) {
    if (time == null) {
      return StringUtil.EMPTY;
    }
    DateFormat df = new SimpleDateFormat(format);
    return df.format(time);
  }

  public static LocalDateTime format(String time, String format) {
    if (time == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(time, formatter);
  }

  public static String formatDate(String strDate, String inputFormat, String outputFormat) {
    if (ObjectUtils.isNotEmpty(strDate)) {
      DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
      DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
      try {
        LocalDateTime dateTime = LocalDateTime.parse(strDate, inputFormatter);
        return dateTime.format(outputFormatter);
      } catch (DateTimeParseException e) {
        log.error("Parse data error: {}", e.getMessage());
      }
    }
    return "";
  }

  public static LocalDate formatLocalDate(String time, String format) {
    if (time == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(time, formatter);
  }
}

