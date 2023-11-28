package com.betrybe.agrix.controllers;

import com.betrybe.agrix.controllers.dto.CropDto;
import com.betrybe.agrix.models.entities.Crop;
import com.betrybe.agrix.models.entities.Farm;
import com.betrybe.agrix.services.CropService;
import com.betrybe.agrix.services.FarmService;
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
 * Class CropController.
 */
@RestController
@RequestMapping()
public class CropController {

  private CropService cropService;
  private FarmService farmService;
  
  public CropController(CropService cropService, FarmService farmService) {
    this.cropService = cropService;
    this.farmService = farmService;
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
}