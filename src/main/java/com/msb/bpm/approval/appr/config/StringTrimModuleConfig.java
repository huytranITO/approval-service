package com.msb.bpm.approval.appr.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;

/*
* @author: BaoNV2
* @since: 26/9/2023 1:19 PM
*
* */
public class StringTrimModuleConfig extends SimpleModule {
  public StringTrimModuleConfig() {
    addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
      @Override
      public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
          JsonProcessingException {
        return StringUtils.trim(jp.getValueAsString());
      }
    });
  }
}
