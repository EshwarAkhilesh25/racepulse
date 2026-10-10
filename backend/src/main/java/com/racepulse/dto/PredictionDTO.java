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
public class PredictionDTO {
  private String modelType; // "Statistical Multi-Variable Trend Projection (Statistical Estimate)"
  private String disclaimer; // "Estimates calculated from rolling historical session timing, qualifying conversion rates, and finishing variance."
  private String nextRaceName;
  private List<DriverPrediction> driverPredictions;
  private List<ChampionshipProjection> driverChampionshipProjections;
  private List<ChampionshipProjection> constructorChampionshipProjections;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DriverPrediction {
    private String driverCode;
    private String driverName;
    private String constructorName;
    private String teamColor;
    private int expectedFinishMin;
    private int expectedFinishMax;
    private int expectedFinishMedian;
    private String recentTrend; // UPWARD, STABLE, DOWNWARD
    private double qualifyingConversionRate; // % of time starting top 5 converts to podium
    private double confidenceIndex; // % confidence metric based on sample size
    private String statisticalNote;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChampionshipProjection {
    private String name;
    private String teamColor;
    private int currentPoints;
    private int projectedEndSeasonPointsMin;
    private int projectedEndSeasonPointsMax;
    private int projectedEndSeasonPointsMedian;
    private int projectedFinalPosition;
  }
}
