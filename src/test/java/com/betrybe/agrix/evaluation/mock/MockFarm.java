package com.betrybe.agrix.evaluation.mock;

import java.util.HashMap;
import java.util.Map;

public class MockFarm extends HashMap<String, Object> {

  public <K, V> MockFarm() {
    super();
  }

  public <K, V> MockFarm(Map<K, V> source) {
    super((Map<String, Object>) source);
  }
}
