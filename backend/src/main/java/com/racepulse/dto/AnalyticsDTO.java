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
public class AnalyticsDTO {
  private List<DriverConsistencyMetric> driverConsistency;
  private List<QualiVsRaceMetric> qualiVsRacePerformance;
  private List<PositionGainLossMetric> positionGainLoss;
  private List<PointsProgressionMetric> pointsProgression;
  private List<ConstructorPaceMetric> constructorPaceAnalysis;
  private List<TeammateComparisonMetric> teammateHeadToHead;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DriverConsistencyMetric {
    private String driverCode;
    private String driverName;
    private String constructorName;
    private String teamColor;
    private double consistencyScore; // 0-100 index
    private double finishVariance;
    private double avgLapTimeDeltaMs;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QualiVsRaceMetric {
    private String driverCode;
    private String driverName;
    private String teamColor;
    private double avgQualifyingPosition;
    private double avgFinishPosition;
    private double netDelta; // negative means gains positions in race
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PositionGainLossMetric {
    private String driverCode;
    private String driverName;
    private String teamColor;
    private int positionsGained;
    private int positionsLost;
    private int netPositionsGained;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PointsProgressionMetric {
    private String entityName; // Driver or Constructor
    private String teamColor;
    private List<RoundPoints> rounds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundPoints {
      private int round;
      private String raceName;
      private int cumulativePoints;
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ConstructorPaceMetric {
    private String constructorName;
    private String teamColor;
    private double avgQualiGapToPoleSec;
    private double avgRacePaceGapSec;
    private double developmentRateScore;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TeammateComparisonMetric {
    private String constructorName;
    private String driver1Code;
    private String driver2Code;
    private int driver1Points;
    private int driver2Points;
    private int qualiH2HDriver1;
    private int qualiH2HDriver2;
    private int raceH2HDriver1;
    private int raceH2HDriver2;
  }
}
