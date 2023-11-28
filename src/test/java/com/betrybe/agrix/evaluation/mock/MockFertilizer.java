package com.betrybe.agrix.evaluation.mock;

import java.util.HashMap;
import java.util.Map;

public class MockFertilizer extends HashMap<String, Object> {

  public <K, V> MockFertilizer() {
    super();
  }

  public <K, V> MockFertilizer(Map<K,V> source) {
    super((Map<String, Object>) source);
  }

  public MockFertilizer clone() {
    return new MockFertilizer(this);
  }
}
