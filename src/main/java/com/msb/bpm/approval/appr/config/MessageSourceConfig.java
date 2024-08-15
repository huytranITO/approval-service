package com.msb.bpm.approval.appr.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static com.msb.bpm.approval.appr.constant.Constant.DEFAULT_ENCODING;
import static com.msb.bpm.approval.appr.constant.Constant.MESSAGE_SOURCE_BASE_NAMES;
import static com.msb.bpm.approval.appr.constant.Constant.VI_LANG;

@Configuration
public class MessageSourceConfig {

  @Bean
  MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames(MESSAGE_SOURCE_BASE_NAMES);
    messageSource.setDefaultEncoding(DEFAULT_ENCODING);
    messageSource.setDefaultLocale(Locale.forLanguageTag(VI_LANG));
    return messageSource;
  }

  @Bean
  LocalValidatorFactoryBean getValidator() {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setValidationMessageSource(messageSource());
    return validatorFactoryBean;
  }

  @Bean
  LocaleResolver localeResolver() {
    return new CustomLocaleResolver();
  }
}
