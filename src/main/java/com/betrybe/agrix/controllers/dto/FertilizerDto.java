package com.betrybe.agrix.controllers.dto;

import com.betrybe.agrix.models.entities.Fertilizer;

/**
 * FertilizerDto.
 */
public record FertilizerDto(String name, String brand, String composition) {

  public Fertilizer toFertilizer() {
    return new Fertilizer(name, brand, composition);
  }

  /**
   * ToResponse.
   */
  public record ToResponse(Long id, String name, String brand, String composition) {

  }

  public static ToResponse fromEntity(Fertilizer fertilizer) {
    return new ToResponse(fertilizer.getId(), fertilizer.getName(),
      fertilizer.getBrand(), fertilizer.getComposition());
  }
}