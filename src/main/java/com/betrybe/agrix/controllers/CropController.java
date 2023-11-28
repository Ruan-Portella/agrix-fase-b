package com.betrybe.agrix.controllers;

import com.betrybe.agrix.controllers.dto.CropDto;
import com.betrybe.agrix.controllers.dto.FertilizerDto;
import com.betrybe.agrix.models.entities.Crop;
import com.betrybe.agrix.models.entities.Farm;
import com.betrybe.agrix.models.entities.Fertilizer;
import com.betrybe.agrix.services.CropService;
import com.betrybe.agrix.services.FarmService;
import com.betrybe.agrix.services.FertilizerService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class CropController.
 */
@RestController
@RequestMapping()
public class CropController {

  private CropService cropService;
  private FarmService farmService;
  private FertilizerService fertilizerService;
  
  /**
    * Método cropController.
    */
  public CropController(CropService cropService, FarmService farmService, 
      FertilizerService fertilizerService) {
    this.cropService = cropService;
    this.farmService = farmService;
    this.fertilizerService = fertilizerService;
  }

  /**
 * Método createCrop.
 */
  @PostMapping("/farms/{farmId}/crops")
  public ResponseEntity<?> createCrop(@PathVariable Long farmId,
      @RequestBody CropDto cropDto) {
    Optional<Farm> optionalFarm = farmService.getFarmById(farmId);
    if (optionalFarm.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fazenda não encontrada!");
    }
    return optionalFarm.map(farm -> {
      Crop crop = cropDto.toCrop();
      crop.setFarm(farm);
      crop.setPlantedDate(cropDto.getPlantedDate());
      crop.setHarvestDate(cropDto.getHarvestDate());
      Crop savedCrop = cropService.createCrop(crop);
      CropDto.ToResponse response = CropDto.fromEntity(savedCrop);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }).orElse(ResponseEntity.notFound().build());
  }

  /**
 * Método getCropFarm.
 */
  @GetMapping("/farms/{farmId}/crops")
  public ResponseEntity<?> getCropFarm(@PathVariable Long farmId) {
    Optional<Farm> farm = farmService.getFarmById(farmId);
    if (farm.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fazenda não encontrada!");
    }
    List<Crop> crops = farm.get().getCrops();
    List<CropDto.ToResponse> cropResponse = crops.stream().map(CropDto::fromEntity).toList();
    return ResponseEntity.status(HttpStatus.OK).body(cropResponse);
  }

  /**
   * Método getAllCrops.
   */
  @GetMapping("/crops")
  public ResponseEntity<?> getAllCrops() {
    List<Crop> crops = cropService.getAllCrops();

    List<CropDto.ToResponse> cropDto = crops.stream().map(CropDto::fromEntity).toList();
    return ResponseEntity.status(HttpStatus.OK).body(cropDto);
  }

  /**
   * Método getCropById.
   */
  @GetMapping("/crops/{id}")
  public ResponseEntity<?> getCropById(@PathVariable Long id) {
    Optional<Crop> cropOptional = cropService.getCropById(id);
    if (cropOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plantação não encontrada!");
    }
    return cropOptional.map(crop -> {
      CropDto.ToResponse response = CropDto.fromEntity(crop);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }).orElse(ResponseEntity.notFound().build());
  }

  /**
   * Método getCropByHarvestDateBetween.
   */
  @GetMapping("/crops/search")
  public ResponseEntity<?> getCropByHarvestDateBetween(@RequestParam String start, 
      @RequestParam String end) {
    LocalDate startDate = LocalDate.parse(start);
    LocalDate endDate = LocalDate.parse(end);
    List<Crop> crops = cropService.getCropByHarvestDateBetween(startDate, endDate);
    List<CropDto.ToResponse> cropDto = crops.stream().map(CropDto::fromEntity).toList();
    return ResponseEntity.status(HttpStatus.OK).body(cropDto);
  }

  /**
   * Método associateCropWithFertilizer.
   */
  @PostMapping("/crops/{cropId}/fertilizers/{fertilizerId}")
  public ResponseEntity<String> associateCropWithFertilizer(@PathVariable Long cropId,
      @PathVariable Long fertilizerId) {

    Optional<Fertilizer> fetlizerById = fertilizerService.getFertilizerById(fertilizerId);
    if (fetlizerById.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fertilizante não encontrado!");
    }

    Optional<Crop> cropById = cropService.getCropById(cropId);
    if (cropById.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plantação não encontrada!");
    }

    Crop crop = cropById.get();
    Fertilizer fertilizer = fetlizerById.get();

    if (crop.getFertilizers().contains(fertilizer)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fertilizante não encontrado!");
    }

    crop.setFertilizer(fertilizer);
    cropService.createCrop(crop);

    return ResponseEntity.status(HttpStatus.CREATED)
      .body("Fertilizante e plantação associados com sucesso!");
  }

  /**
   * Método getFertilizersByCropId.
   */
  @GetMapping("/crops/{cropId}/fertilizers")
  public ResponseEntity<?> getFertilizersByCropId(@PathVariable Long cropId) {
    Optional<Crop> cropById = cropService.getCropById(cropId);
    if (cropById.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plantação não encontrada!");
    }

    Crop crop = cropById.get();
    List<Fertilizer> allFertilizer = crop.getFertilizers();

    List<FertilizerDto.ToResponse> fertilizerToDto = allFertilizer.stream()
        .map(FertilizerDto::fromEntity).toList();

    return ResponseEntity.status(HttpStatus.OK).body(fertilizerToDto);
  }
}