package com.betrybe.agrix.evaluation.mock;

import java.util.Map;

public class FertilizerFixtures {
  public final static MockFertilizer fertilizer1 = new MockFertilizer(Map.of(
      "name", "Compostagem",
      "brand", "Feito em casa",
      "composition", "Restos de alimento"
  ));

  public final static MockFertilizer fertilizer2 = new MockFertilizer(Map.of(
      "name", "Húmus",
      "brand", "Feito pelas minhocas",
      "composition", "Muitos nutrientes"
  ));

  public final static MockFertilizer fertilizer3 = new MockFertilizer(Map.of(
      "name", "Adubo",
      "brand", "Feito pelas vaquinhas",
      "composition", "Esterco"
  ));
}
