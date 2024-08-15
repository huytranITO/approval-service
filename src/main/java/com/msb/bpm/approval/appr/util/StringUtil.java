package com.msb.bpm.approval.appr.util;

public class StringUtil {

  public static final String EMPTY = "";
  public static final String COMMA = ",";
  public static final String APOSTROPHE = "'";
  public static final String UNDERSCORE = "_";


  public static final String COMMA_AND_APOSTROPHE = "', '";


  public static boolean isEmpty(String str) {
    if (null == str) {
      return true;
    }
    if (str.isEmpty()) {
      return true;
    }
    return false;
  }

  public static String getValue(String input) {
    if (null == input) {
      return "";
    }
    return input;
  }

  public static String getBiggestValue(String value1, String value2) {
    if (value1 == null) {
      return value2;
    }
    if (value2 == null) {
      return value1;
    }
    return value1.compareTo(value2) >= 0 ? value1 : value2;
  }

  public static boolean isLessThan(String a, String b) {
    return compare(a, b) < 0;
  }

  public static int compare(String a, String b) {
    if (a == null && b == null) {
      return 0;
    }

    if (a == null) {
      return -1;
    }

    if (b == null) {
      return 1;
    }

    return a.compareTo(b);
  }
}
