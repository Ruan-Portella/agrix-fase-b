package com.betrybe.agrix.evaluation.mock;

import java.util.Map;

public class FarmFixtures {

  public final static MockFarm farm1 = new MockFarm(Map.of(
      "name", "Fazenda do Júlio",
      "size", 2.5
  ));

  public final static MockFarm farm2 = new MockFarm(Map.of(
      "name", "My Cabbages!",
      "size", 3.49
  ));

  public final static MockFarm farm3 = new MockFarm(Map.of(
      "name", "Fazendinha",
      "size", 5
  ));
}
