package com.betrybe.agrix.evaluation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class TestHelpers implements ApplicationContextAware {

  private static ObjectMapper objectMapper;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    TestHelpers.objectMapper = applicationContext.getBean(ObjectMapper.class);
  }

  public static String objectToJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
