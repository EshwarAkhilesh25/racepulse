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
public class ConstructorStatsDTO {
  private ConstructorDTO constructor;
  private List<Integer> recentFormPoints;
  private double avgQualifyingPosition;
  private double avgRacePaceIndex;
  private DriverComparison driverComparison;
  private List<HistoricalSeasonStat> historicalPerformance;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DriverComparison {
    private DriverDTO driver1;
    private DriverDTO driver2;
    private int driver1Points;
    private int driver2Points;
    private int driver1QualiHeadToHead;
    private int driver2QualiHeadToHead;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class HistoricalSeasonStat {
    private int season;
    private int position;
    private int points;
    private int wins;
  }
}
