package com.racepulse.service;

import com.racepulse.dto.DashboardDTO;
import com.racepulse.dto.DriverDTO;
import com.racepulse.dto.LiveSessionDTO;
import com.racepulse.dto.RaceScheduleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

      List<DriverDTO> drivers = standings.getDrivers();
      DriverDTO leader = (drivers != null && !drivers.isEmpty()) ? drivers.get(0) : null;
      DriverDTO second = (drivers != null && drivers.size() > 1) ? drivers.get(1) : null;
      int gap = (leader != null && second != null) ? (leader.getPoints() - second.getPoints()) : 19;

      List<DashboardDTO.RaceAlertDTO> alerts = new ArrayList<>();
      if (leader != null && second != null) {
        alerts.add(DashboardDTO.RaceAlertDTO.builder()
          .id("alt-1")
          .timestamp("Championship Update")
          .category("STANDINGS")
          .title(leader.getFullName() + " leads Drivers' Championship")
          .message(leader.getFullName() + " holds a " + gap + "-point advantage over " + second.getFullName() + ".")
          .severity("INFO")
          .build());
      }

      alerts.add(DashboardDTO.RaceAlertDTO.builder()
        .id("alt-2")
        .timestamp("Next Event")
        .category("CALENDAR")
        .title("Round " + nextRace.getRound() + ": " + nextRace.getRaceName())
        .message("Preparations underway for " + nextRace.getCircuitName() + " in " + nextRace.getCity() + ", " + nextRace.getCountry() + ".")
        .severity("INFO")
        .build());

      alerts.add(DashboardDTO.RaceAlertDTO.builder()
        .id("alt-3")
        .timestamp("Session Status")
        .category("TELEMETRY")
        .title("RacePulse Live Stream: " + liveSession.getSessionStatus())
        .message("Active telemetry stream: " + liveSession.getSessionName())
        .severity("SUCCESS")
        .build());

      DashboardDTO.TrackInfoDTO track = resolveTrackInfo(nextRace.getCircuitName(), nextRace.getCountry());

      LiveSessionDTO.WeatherInfoDTO weather = liveSession.getWeather() != null
        ? liveSession.getWeather()
        : LiveSessionDTO.WeatherInfoDTO.builder()
            .airTempCelsius(28.4)
            .trackTempCelsius(39.2)
            .humidityPercent(60.0)
            .windSpeedKmh(11.0)
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

  private DashboardDTO.TrackInfoDTO resolveTrackInfo(String circuitName, String country) {
    if (circuitName == null) circuitName = "";
    String c = circuitName.toLowerCase();

    if (c.contains("marina bay") || c.contains("singapore")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Marina Bay Street Circuit").location("Singapore").lengthKm(4.940).corners(19).drsZones(3).lapRecord("1:35.867").lapRecordHolder("Lewis Hamilton (2023)").build();
    } else if (c.contains("americas") || c.contains("austin")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Circuit of the Americas").location("Austin, USA").lengthKm(5.513).corners(20).drsZones(2).lapRecord("1:36.169").lapRecordHolder("Charles Leclerc (2019)").build();
    } else if (c.contains("hermanos") || c.contains("mexico")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Autódromo Hermanos Rodríguez").location("Mexico City, Mexico").lengthKm(4.304).corners(17).drsZones(3).lapRecord("1:17.774").lapRecordHolder("Valtteri Bottas (2021)").build();
    } else if (c.contains("interlagos") || c.contains("pace") || c.contains("brazil") || c.contains("paulo")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Autódromo José Carlos Pace").location("São Paulo, Brazil").lengthKm(4.309).corners(15).drsZones(2).lapRecord("1:10.540").lapRecordHolder("Valtteri Bottas (2018)").build();
    } else if (c.contains("vegas")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Las Vegas Strip Circuit").location("Las Vegas, USA").lengthKm(6.201).corners(17).drsZones(2).lapRecord("1:35.490").lapRecordHolder("Oscar Piastri (2023)").build();
    } else if (c.contains("lusail") || c.contains("qatar")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Lusail International Circuit").location("Lusail, Qatar").lengthKm(5.419).corners(16).drsZones(1).lapRecord("1:24.319").lapRecordHolder("Max Verstappen (2023)").build();
    } else if (c.contains("yas") || c.contains("abu dhabi")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Yas Marina Circuit").location("Abu Dhabi, UAE").lengthKm(5.281).corners(16).drsZones(2).lapRecord("1:26.103").lapRecordHolder("Max Verstappen (2021)").build();
    } else if (c.contains("albert") || c.contains("australia")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Albert Park Circuit").location("Melbourne, Australia").lengthKm(5.278).corners(14).drsZones(4).lapRecord("1:19.813").lapRecordHolder("Charles Leclerc (2024)").build();
    } else if (c.contains("bahrain") || c.contains("sakhir")) {
      return DashboardDTO.TrackInfoDTO.builder().name("Bahrain International Circuit").location("Sakhir, Bahrain").lengthKm(5.412).corners(15).drsZones(3).lapRecord("1:31.447").lapRecordHolder("Pedro de la Rosa (2005)").build();
    }

    return DashboardDTO.TrackInfoDTO.builder()
      .name(circuitName.isEmpty() ? "Grand Prix Circuit" : circuitName)
      .location(country != null ? country : "World Championship")
      .lengthKm(5.200)
      .corners(16)
      .drsZones(2)
      .lapRecord("1:29.800")
      .lapRecordHolder("FIA Official Record")
      .build();
  }
}
