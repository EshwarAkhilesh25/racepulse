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
public class DashboardDTO {
  private RaceScheduleDTO nextRace;
  private RaceScheduleDTO latestRace;
  private List<DriverDTO> topDrivers;
  private List<ConstructorDTO> topConstructors;
  private LiveSessionDTO liveSessionSnapshot;
  private List<RaceAlertDTO> recentAlerts;
  private TrackInfoDTO trackInfo;
  private LiveSessionDTO.WeatherInfoDTO currentWeather;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RaceAlertDTO {
    private String id;
    private String timestamp;
    private String category; // TIMING, INCIDENT, WEATHER, CHAMPIONSHIP
    private String title;
    private String message;
    private String severity; // INFO, WARNING, URGENT
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TrackInfoDTO {
    private String name;
    private String location;
    private double lengthKm;
    private int corners;
    private int drsZones;
    private String lapRecord;
    private String lapRecordHolder;
  }
}
