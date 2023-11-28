package com.betrybe.agrix.evaluation.mock;

import java.util.Map;

public class CropFixtures {

  public final static MockCrop crop1 = new MockCrop(Map.of(
      "name", "Couve-flor",
      "plantedArea", 12.21,
      "plantedDate", "2023-01-02",
      "harvestDate", "2023-02-02"
  ));

  public final static MockCrop crop2 = new MockCrop(Map.of(
      "name", "Abobrinha",
      "plantedArea", 34.45,
      "plantedDate", "2023-02-03",
      "harvestDate", "2023-05-03"
  ));

  public final static MockCrop crop3 = new MockCrop(Map.of(
      "name", "Tomate",
      "plantedArea", 7.77,
      "plantedDate", "2023-03-04",
      "harvestDate", "2023-07-04"
  ));

  public final static MockCrop crop4 = new MockCrop(Map.of(
      "name", "Alface",
      "plantedArea", 32.21,
      "plantedDate", "2023-04-05",
      "harvestDate", "2023-09-05"
  ));

  public final static MockCrop crop5 = new MockCrop(Map.of(
      "name", "Rúcula",
      "plantedArea", 1.1,
      "plantedDate", "2023-05-06",
      "harvestDate", "2023-08-06"
  ));
}
