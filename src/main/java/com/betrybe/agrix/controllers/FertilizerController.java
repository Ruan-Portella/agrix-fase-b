package com.betrybe.agrix.controllers;

import com.betrybe.agrix.controllers.dto.FertilizerDto;
import com.betrybe.agrix.models.entities.Fertilizer;
import com.betrybe.agrix.services.FertilizerService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class FertilizerController.
 */
@RestController
@RequestMapping("/fertilizers")
public class FertilizerController {

  private final FertilizerService fertilizerService;

  public FertilizerController(FertilizerService fertilizerService) {
    this.fertilizerService = fertilizerService;
  }

  /**
   * Método createFertilizer.
   */
  @PostMapping()
  public ResponseEntity<?> createFertilizer(@RequestBody FertilizerDto fertilizerDto) {
    Fertilizer fertilizer = fertilizerDto.toFertilizer();
    Fertilizer savedFertilizer = fertilizerService.createFertilizer(fertilizer);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedFertilizer);
  }

  /**
   * Método getAllFertilizers.
   */
  @GetMapping()
  public ResponseEntity<List<Fertilizer>> getAllFertilizers() {
    List<Fertilizer> allFertilizers = fertilizerService.getAllFertilizers();

    return ResponseEntity.ok(allFertilizers);
  }

  /**
   * Método getFertilizerById.
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getFertilizerById(@PathVariable Long id) {
    Optional<Fertilizer> optionalFertilizer = fertilizerService.getFertilizerById(id);
    if (optionalFertilizer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fertilizante não encontrado!");
    }
    
    return ResponseEntity.status(HttpStatus.OK).body(optionalFertilizer.get());
  }
}