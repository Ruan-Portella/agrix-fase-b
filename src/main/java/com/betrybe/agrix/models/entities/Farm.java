package com.betrybe.agrix.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Class Farm.
 */
@Entity
@Table(name = "farms")
public class Farm {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private Double size;
  
  @JsonIgnore
  @OneToMany(mappedBy = "farm")
  private List<Crop> crops;

  public Farm() {}

  /**
 * Metodo contrutor.
*/
  public Farm(Long id, String name, Double size) {
    this.id = id;
    this.name = name;
    this.size = size;
  }

  public Farm(String name, double size) {
    this.setName(name);
    this.setSize(size);
  }
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getSize() {
    return size;
  }

  public void setSize(Double size) {
    this.size = size;
  }
  
  public List<Crop> getCrops() {
    return crops;
  }
  
  public void setCrops(List<Crop> crops) {
    this.crops = crops;
  }
}