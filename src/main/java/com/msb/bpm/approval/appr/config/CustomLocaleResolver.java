package com.msb.bpm.approval.appr.config;

import static com.msb.bpm.approval.appr.constant.Constant.ACCEPT_LANGUAGE;
import static com.msb.bpm.approval.appr.constant.Constant.EN_LANG;
import static com.msb.bpm.approval.appr.constant.Constant.VI_LANG;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

  private static final List<Locale> LOCALES = Arrays.asList(new Locale(EN_LANG), new Locale(VI_LANG));

  @NotNull
  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String headerLang = request.getHeader(ACCEPT_LANGUAGE);
    return headerLang == null || headerLang.isEmpty()
        ? Locale.forLanguageTag(VI_LANG)
        : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
  }
}
