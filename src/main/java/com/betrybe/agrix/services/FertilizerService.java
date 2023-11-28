package com.betrybe.agrix.services;

import com.betrybe.agrix.models.entities.Fertilizer;
import com.betrybe.agrix.models.repositories.FertilizerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Class FertilizerService.
 */
@Service
public class FertilizerService {

  private final FertilizerRepository fertilizerRepository;

  public FertilizerService(FertilizerRepository fertilizerRepository) {
    this.fertilizerRepository = fertilizerRepository;
  }

  //Método createFarm.
  public Fertilizer createFertilizer(Fertilizer fertilizer) {
    return fertilizerRepository.save(fertilizer);
  }

  //Método getAllFertilizers.
  public List<Fertilizer> getAllFertilizers() {
    return fertilizerRepository.findAll();
  }

  //Método getFertilizerById.
  public Optional<Fertilizer> getFertilizerById(Long fertilizerId) {
    return fertilizerRepository.findById(fertilizerId);
  }
}