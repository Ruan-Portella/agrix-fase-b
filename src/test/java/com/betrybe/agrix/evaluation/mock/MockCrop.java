package com.betrybe.agrix.evaluation.mock;

import java.util.HashMap;
import java.util.Map;

public class MockCrop extends HashMap<String, Object> {

  public <K, V> MockCrop() {
    super();
  }

  public <K, V> MockCrop(Map<K, V> source) {
    super((Map<String, Object>) source);
  }

  public MockCrop clone() {
    return new MockCrop(this);
  }
}
