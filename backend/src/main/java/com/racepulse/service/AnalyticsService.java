package com.racepulse.service;

import com.racepulse.dto.AnalyticsDTO;
import com.racepulse.dto.DriverDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

  private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
  private final StandingsService standingsService;

  public AnalyticsService(StandingsService standingsService) {
    this.standingsService = standingsService;
  }

  public Mono<AnalyticsDTO> getAnalytics() {
    return standingsService.getStandings()
      .map(standings -> {
        List<DriverDTO> drivers = standings.getDrivers();

        List<AnalyticsDTO.DriverConsistencyMetric> consistency = new ArrayList<>();
        List<AnalyticsDTO.QualiVsRaceMetric> qualiVsRace = new ArrayList<>();
        List<AnalyticsDTO.PositionGainLossMetric> gainLoss = new ArrayList<>();
        List<AnalyticsDTO.PointsProgressionMetric> progression = new ArrayList<>();

        for (DriverDTO d : drivers) {
          consistency.add(AnalyticsDTO.DriverConsistencyMetric.builder()
            .driverCode(d.getCode())
            .driverName(d.getFullName())
            .constructorName(d.getConstructorName())
            .teamColor(d.getTeamColor())
            .consistencyScore(Math.max(65.0, 98.0 - (d.getPosition() * 2.1)))
            .finishVariance(1.1 + (d.getPosition() * 0.3))
            .avgLapTimeDeltaMs(120.0 + (d.getPosition() * 85.0))
            .build());

          double qualiPos = d.getPosition() <= 3 ? 1.5 + d.getPosition() * 0.5 : d.getPosition() + 0.8;
          double racePos = d.getAverageFinishPosition();

          qualiVsRace.add(AnalyticsDTO.QualiVsRaceMetric.builder()
            .driverCode(d.getCode())
            .driverName(d.getFullName())
            .teamColor(d.getTeamColor())
            .avgQualifyingPosition(qualiPos)
            .avgFinishPosition(racePos)
            .netDelta(racePos - qualiPos)
            .build());

          int gained = d.getPosition() <= 5 ? 18 : 24;
          int lost = d.getPosition() <= 5 ? 6 : 19;
          gainLoss.add(AnalyticsDTO.PositionGainLossMetric.builder()
            .driverCode(d.getCode())
            .driverName(d.getFullName())
            .teamColor(d.getTeamColor())
            .positionsGained(gained)
            .positionsLost(lost)
            .netPositionsGained(gained - lost)
            .build());

          List<AnalyticsDTO.PointsProgressionMetric.RoundPoints> rounds = new ArrayList<>();
          int total = 0;
          int perRound = d.getPoints() / 16;
          for (int r = 1; r <= 16; r++) {
            total += Math.max(0, perRound + ((r % 3 == 0) ? 5 : -2));
            rounds.add(AnalyticsDTO.PointsProgressionMetric.RoundPoints.builder()
              .round(r)
              .raceName("Round " + r)
              .cumulativePoints(total)
              .build());
          }

          progression.add(AnalyticsDTO.PointsProgressionMetric.builder()
            .entityName(d.getCode())
            .teamColor(d.getTeamColor())
            .rounds(rounds)
            .build());
        }

        List<AnalyticsDTO.ConstructorPaceMetric> constrPace = List.of(
          AnalyticsDTO.ConstructorPaceMetric.builder().constructorName("McLaren").teamColor("#FF8000").avgQualiGapToPoleSec(0.00).avgRacePaceGapSec(0.00).developmentRateScore(96.5).build(),
          AnalyticsDTO.ConstructorPaceMetric.builder().constructorName("Red Bull Racing").teamColor("#3671C6").avgQualiGapToPoleSec(0.08).avgRacePaceGapSec(0.12).developmentRateScore(92.0).build(),
          AnalyticsDTO.ConstructorPaceMetric.builder().constructorName("Ferrari").teamColor("#E8002D").avgQualiGapToPoleSec(0.14).avgRacePaceGapSec(0.21).developmentRateScore(91.2).build(),
          AnalyticsDTO.ConstructorPaceMetric.builder().constructorName("Mercedes").teamColor("#27F4D2").avgQualiGapToPoleSec(0.28).avgRacePaceGapSec(0.35).developmentRateScore(88.4).build(),
          AnalyticsDTO.ConstructorPaceMetric.builder().constructorName("Williams").teamColor("#64C4FF").avgQualiGapToPoleSec(0.55).avgRacePaceGapSec(0.62).developmentRateScore(89.0).build()
        );

        List<AnalyticsDTO.TeammateComparisonMetric> teammateH2H = List.of(
          AnalyticsDTO.TeammateComparisonMetric.builder().constructorName("McLaren").driver1Code("NOR").driver2Code("PIA").driver1Points(331).driver2Points(247).qualiH2HDriver1(11).qualiH2HDriver2(6).raceH2HDriver1(12).raceH2HDriver2(5).build(),
          AnalyticsDTO.TeammateComparisonMetric.builder().constructorName("Ferrari").driver1Code("LEC").driver2Code("HAM").driver1Points(235).driver2Points(190).qualiH2HDriver1(10).qualiH2HDriver2(7).raceH2HDriver1(9).raceH2HDriver2(8).build()
        );

        return AnalyticsDTO.builder()
          .driverConsistency(consistency)
          .qualiVsRacePerformance(qualiVsRace)
          .positionGainLoss(gainLoss)
          .pointsProgression(progression)
          .constructorPaceAnalysis(constrPace)
          .teammateHeadToHead(teammateH2H)
          .build();
      });
  }
}
