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
public class LiveSessionDTO {
  private String sessionKey;
  private String sessionName;
  private String sessionType; // Race, Qualifying, FP1, FP2, FP3, Sprint
  private String circuitName;
  private String countryName;
  private int currentLap;
  private int totalLaps;
  private String sessionStatus; // LIVE, SUSPENDED, COMPLETED, UPCOMING
  private List<DriverPositionDTO> positions;
  private List<RaceIncidentDTO> recentIncidents;
  private WeatherInfoDTO weather;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DriverPositionDTO {
    private int position;
    private int driverNumber;
    private String driverCode;
    private String driverName;
    private String constructorName;
    private String teamColor;
    private String gapToLeader;
    private String intervalToAhead;
    private String lastLapTime;
    private String bestLapTime;
    private int currentStintLaps;
    private String tyreCompound; // SOFT, MEDIUM, HARD, INTERMEDIATE, WET
    private boolean isPitting;
    private int pitStopsCount;
    private String sector1;
    private String sector2;
    private String sector3;
    private int positionDelta; // +2, -1, etc.
    private String status; // ON_TRACK, IN_PIT, RETIRED
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RaceIncidentDTO {
    private String time;
    private String lap;
    private String driverCode;
    private String incidentType; // YELLOW_FLAG, SAFETY_CAR, VIRTUAL_SAFETY_CAR, INVESTIGATION, PENALTY
    private String description;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WeatherInfoDTO {
    private double airTempCelsius;
    private double trackTempCelsius;
    private double humidityPercent;
    private double windSpeedKmh;
    private String rainfall; // NONE, LIGHT, HEAVY
  }
}
