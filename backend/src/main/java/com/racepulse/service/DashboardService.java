package com.racepulse.service;

import com.racepulse.dto.DashboardDTO;
import com.racepulse.dto.LiveSessionDTO;
import com.racepulse.dto.RaceScheduleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DashboardService {

  private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

  private final StandingsService standingsService;
  private final ScheduleService scheduleService;
  private final LiveRaceService liveRaceService;

  public DashboardService(StandingsService standingsService,
                          ScheduleService scheduleService,
                          LiveRaceService liveRaceService) {
    this.standingsService = standingsService;
    this.scheduleService = scheduleService;
    this.liveRaceService = liveRaceService;
  }

  public Mono<DashboardDTO> getDashboardData() {
    return Mono.zip(
      standingsService.getStandings(),
      scheduleService.getSchedule(),
      liveRaceService.getLiveSession()
    ).map(tuple -> {
      var standings = tuple.getT1();
      var schedule = tuple.getT2();
      var liveSession = tuple.getT3();

      RaceScheduleDTO nextRace = schedule.stream()
        .filter(r -> "UPCOMING".equalsIgnoreCase(r.getStatus()) || "LIVE".equalsIgnoreCase(r.getStatus()))
        .findFirst()
        .orElse(schedule.get(schedule.size() - 1));

      RaceScheduleDTO latestRace = schedule.stream()
        .filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()))
        .reduce((first, second) -> second)
        .orElse(schedule.get(0));

      List<DashboardDTO.RaceAlertDTO> alerts = List.of(
        DashboardDTO.RaceAlertDTO.builder().id("alt-1").timestamp("Just now").category("TIMING").title("Norris sets Purple Sector 1").message("Lando Norris recorded 28.102s in S1, 0.088s faster than Verstappen.").severity("INFO").build(),
        DashboardDTO.RaceAlertDTO.builder().id("alt-2").timestamp("5m ago").category("INCIDENT").title("Track Limits Warning").message("Fernando Alonso warned for track limits at Turn 7.").severity("WARNING").build(),
        DashboardDTO.RaceAlertDTO.builder().id("alt-3").timestamp("12m ago").category("CHAMPIONSHIP").title("Standings Gap Tightens").message("Live projected points reduce Norris lead to 14 points.").severity("INFO").build()
      );

      DashboardDTO.TrackInfoDTO track = DashboardDTO.TrackInfoDTO.builder()
        .name("Marina Bay Street Circuit")
        .location("Singapore")
        .lengthKm(4.940)
        .corners(19)
        .drsZones(3)
        .lapRecord("1:35.867")
        .lapRecordHolder("Lewis Hamilton (2023)")
        .build();

      LiveSessionDTO.WeatherInfoDTO weather = LiveSessionDTO.WeatherInfoDTO.builder()
        .airTempCelsius(29.4)
        .trackTempCelsius(41.8)
        .humidityPercent(68.0)
        .windSpeedKmh(12.5)
        .rainfall("NONE")
        .build();

      return DashboardDTO.builder()
        .nextRace(nextRace)
        .latestRace(latestRace)
        .topDrivers(standings.getDrivers())
        .topConstructors(standings.getConstructors())
        .liveSessionSnapshot(liveSession)
        .recentAlerts(alerts)
        .trackInfo(track)
        .currentWeather(weather)
        .build();
    });
  }
}
