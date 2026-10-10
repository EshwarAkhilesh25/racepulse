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
public class StandingsDTO {
  private int season;
  private int round;
  private List<DriverDTO> drivers;
  private List<ConstructorDTO> constructors;
}
