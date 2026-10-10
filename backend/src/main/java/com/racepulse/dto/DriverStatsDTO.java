package com.racepulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatsDTO {
  private DriverDTO driver;
  private List<Integer> recentFormPositions;
  private double qualifyingAveragePosition;
  private double raceAveragePosition;
  private int positionGainsTotal;
  private TeammateComparison teammateComparison;
  private List<HistoricalSeasonStat> historicalPerformance;
  private Map<String, Object> telemetrySummary;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TeammateComparison {
    private String teammateId;
    private String teammateName;
    private int qualifyingHeadToHeadWins;
    private int raceHeadToHeadWins;
    private int pointsDifference;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class HistoricalSeasonStat {
    private int season;
    private String constructorName;
    private int position;
    private int points;
    private int wins;
  }
}
