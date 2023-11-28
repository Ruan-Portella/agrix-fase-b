package com.betrybe.agrix.evaluation.util;

import java.nio.charset.StandardCharsets;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public class SimpleResultHandler implements ResultHandler {
  @Override
  public void handle(MvcResult result) throws Exception {
    MockHttpServletResponse response = result.getResponse();
    MockHttpServletRequest request = result.getRequest();

    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(StandardCharsets.UTF_8.toString());
    }

    System.out.println();
    System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());
    System.out.println("Request body: " + request.getContentAsString());
    System.out.println("Response: " + response.getStatus());
    System.out.println("Response body: " + response.getContentAsString());
    System.out.println();
  }
}
