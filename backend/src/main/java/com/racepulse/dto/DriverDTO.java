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
public class DriverDTO {
  private String driverId;
  private int driverNumber;
  private String code;
  private String givenName;
  private String familyName;
  private String fullName;
  private String countryCode;
  private String constructorId;
  private String constructorName;
  private int position;
  private int points;
  private int wins;
  private int podiums;
  private int poles;
  private double averageFinishPosition;
  private String teamColor;
  private String headshotUrl;
}
