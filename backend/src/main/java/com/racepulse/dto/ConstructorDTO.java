package com.racepulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructorDTO {
  private String constructorId;
  private String name;
  private String nationality;
  private int position;
  private int points;
  private int wins;
  private int podiums;
  private int poles;
  private String teamColor;
  private List<DriverDTO> drivers;
}
