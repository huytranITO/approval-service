package com.msb.bpm.approval.appr.config;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/*
 * @author: BaoNV2
 * @update:
 *
 * */
@ControllerAdvice
public class ConfigurationTrimStringParamRequest {
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    StringTrimmerEditor propertyEditor = new StringTrimmerEditor(true);
    binder.registerCustomEditor(String.class, propertyEditor);
  }
}
