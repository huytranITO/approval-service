package com.msb.bpm.approval.appr.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class Constant {

  public static final String MESSAGE_SOURCE_BASE_NAMES = "classpath:messages";
  public static final String DEFAULT_ENCODING = "UTF-8";
  public static final String VI_LANG = "vi";
  public static final String EN_LANG = "en";
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DD_MM_YYYY_HH_MM_SS_FORMAT = "dd/MM/yyyy HH:mm:ss";
  public static final String DD_MM_YYYY_FORMAT = "dd/MM/yyyy";
  public static final String EMAIL_ADDRESS_MSB = "@msb.com.vn";
  public static final String YYYY_MM_DD_FORMAT = "yyyyMMdd";
  public static final String DD_M_YYYY_FORMAT = "dd/M/yyyy";
  public static final String YYYY_MM_DD = "yyyy-MM-dd";

  public static final String MMM_DD_YYYY_HH_MM_SS_A_FORMAT = "MMM dd, yyyy hh:mm:ss a";
  public static final String DD_MM_YYYY_HH_MM_SS_A_FORMAT = "dd/MM/yyyy h:mm:ss a";

  public static final String YYYY_MM_DD_HH_MM_SS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

  public static final String BANGKOK_TIME_ZONE = "Asia/Bangkok";

  public static final String UNDER_SCORE = "_";

  public static final String AUTO_GENERATED = "auto_generated";

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class Cache {
    public static final String CONFIGURATION_CATEGORY_DATA = "CONFIGURATION_CATEGORY_DATA";
    public static final String MERCURY_CATEGORY_DATA = "MERCURY_CATEGORY_DATA";
    public static final String ESB_ACCOUNT_INFO = "ESB_ACCOUNT_INFO";
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class RegexPattern {
    public static final String FULL_NAME_REGEX = "^[a-zA-Z]{1,}(?: [a-zA-Z]+){1,}$";
  }
}
