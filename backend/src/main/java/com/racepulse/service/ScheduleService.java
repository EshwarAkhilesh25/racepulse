package com.racepulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.racepulse.dto.RaceScheduleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

  private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
  private static final String CACHE_KEY_SCHEDULE = "racepulse:schedule:2026";

  private final JolpicaClientService jolpicaClient;
  private final CacheService cacheService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<Integer, String[]> WINNERS_2026 = Map.ofEntries(
    Map.entry(1, new String[]{"Lando Norris", "McLaren"}),
    Map.entry(2, new String[]{"Max Verstappen", "Red Bull Racing"}),
    Map.entry(3, new String[]{"Max Verstappen", "Red Bull Racing"}),
    Map.entry(4, new String[]{"Charles Leclerc", "Ferrari"}),
    Map.entry(5, new String[]{"Lando Norris", "McLaren"}),
    Map.entry(6, new String[]{"Lando Norris", "McLaren"}),
    Map.entry(7, new String[]{"Max Verstappen", "Red Bull Racing"}),
    Map.entry(8, new String[]{"Charles Leclerc", "Ferrari"}),
    Map.entry(9, new String[]{"Max Verstappen", "Red Bull Racing"}),
    Map.entry(10, new String[]{"Max Verstappen", "Red Bull Racing"}),
    Map.entry(11, new String[]{"George Russell", "Mercedes"}),
    Map.entry(12, new String[]{"Lewis Hamilton", "Ferrari"}),
    Map.entry(13, new String[]{"Oscar Piastri", "McLaren"}),
    Map.entry(14, new String[]{"Lewis Hamilton", "Ferrari"}),
    Map.entry(15, new String[]{"Lando Norris", "McLaren"}),
    Map.entry(16, new String[]{"Charles Leclerc", "Ferrari"})
  );

  public ScheduleService(JolpicaClientService jolpicaClient, CacheService cacheService) {
    this.jolpicaClient = jolpicaClient;
    this.cacheService = cacheService;
  }

  @SuppressWarnings("unchecked")
  public Mono<List<RaceScheduleDTO>> getSchedule() {
    List<RaceScheduleDTO> cached = cacheService.get(CACHE_KEY_SCHEDULE, List.class);
    if (cached != null) {
      return Mono.just(cached);
    }

    return jolpicaClient.getCurrentSchedule()
      .map(this::parseSchedule)
      .onErrorResume(e -> {
        log.error("Failed fetching schedule from Jolpica: {}", e.getMessage());
        return Mono.just(createFallbackSchedule());
      })
      .map(schedule -> {
        if (schedule.isEmpty()) schedule = createFallbackSchedule();
        cacheService.put(CACHE_KEY_SCHEDULE, schedule, Duration.ofHours(1));
        return schedule;
      });
  }

  public List<RaceScheduleDTO> parseSchedule(String json) {
    List<RaceScheduleDTO> list = new ArrayList<>();
    if (json == null || json.isBlank()) return list;

    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode racesNode = root.path("MRData").path("RaceTable").path("Races");

      if (racesNode.isArray()) {
        for (JsonNode raceNode : racesNode) {
          int round = raceNode.path("round").asInt(0);
          String raceName = raceNode.path("raceName").asText("Grand Prix");
          String circuitName = raceNode.path("Circuit").path("circuitName").asText("Unknown Circuit");
          JsonNode loc = raceNode.path("Circuit").path("Location");
          String country = loc.path("country").asText("");
          String city = loc.path("locality").asText("");

          String raceDate = raceNode.path("date").asText("");
          String raceTime = raceNode.path("time").asText("14:00:00Z");
          String qualiDate = raceNode.path("Qualifying").path("date").asText("");
          String qualiTime = raceNode.path("Qualifying").path("time").asText("15:00:00Z");

          String raceUTC = raceDate.isEmpty() ? null : (raceDate + "T" + raceTime);
          String qualiUTC = qualiDate.isEmpty() ? null : (qualiDate + "T" + qualiTime);
          boolean hasSprint = raceNode.has("Sprint");

          String status = determineRaceStatus(raceUTC, round);
          String[] winnerInfo = WINNERS_2026.get(round);

          list.add(RaceScheduleDTO.builder()
            .round(round)
            .raceName(raceName)
            .circuitName(circuitName)
            .country(country)
            .city(city)
            .flagEmoji(resolveFlag(country))
            .raceUTC(raceUTC)
            .qualifyingUTC(qualiUTC)
            .hasSprint(hasSprint)
            .status(status)
            .winnerName("COMPLETED".equals(status) && winnerInfo != null ? winnerInfo[0] : null)
            .winningConstructor("COMPLETED".equals(status) && winnerInfo != null ? winnerInfo[1] : null)
            .build());
        }
      }
    } catch (Exception e) {
      log.error("Failed to parse schedule JSON: {}", e.getMessage());
    }

    return list;
  }

  private String determineRaceStatus(String raceUTC, int round) {
    if (raceUTC == null || raceUTC.isBlank()) {
      return round < 17 ? "COMPLETED" : "UPCOMING";
    }
    try {
      Instant raceInstant = Instant.parse(raceUTC);
      Instant now = Instant.now();
      if (now.isAfter(raceInstant.plus(Duration.ofHours(4)))) {
        return "COMPLETED";
      } else if (now.isAfter(raceInstant.minus(Duration.ofHours(1)))) {
        return "LIVE";
      } else {
        return "UPCOMING";
      }
    } catch (Exception e) {
      return round < 17 ? "COMPLETED" : "UPCOMING";
    }
  }

  public List<RaceScheduleDTO> createFallbackSchedule() {
    return List.of(
      RaceScheduleDTO.builder().round(1).raceName("Australian Grand Prix").circuitName("Albert Park Circuit").country("Australia").city("Melbourne").flagEmoji("🇦🇺").raceUTC("2026-03-15T04:00:00Z").qualifyingUTC("2026-03-14T05:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Lando Norris").winningConstructor("McLaren").build(),
      RaceScheduleDTO.builder().round(2).raceName("Chinese Grand Prix").circuitName("Shanghai International Circuit").country("China").city("Shanghai").flagEmoji("🇨🇳").raceUTC("2026-03-22T07:00:00Z").qualifyingUTC("2026-03-21T07:00:00Z").hasSprint(true).status("COMPLETED").winnerName("Max Verstappen").winningConstructor("Red Bull Racing").build(),
      RaceScheduleDTO.builder().round(3).raceName("Japanese Grand Prix").circuitName("Suzuka International Racing Course").country("Japan").city("Suzuka").flagEmoji("🇯🇵").raceUTC("2026-04-05T05:00:00Z").qualifyingUTC("2026-04-04T06:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Max Verstappen").winningConstructor("Red Bull Racing").build(),
      RaceScheduleDTO.builder().round(4).raceName("Bahrain Grand Prix").circuitName("Bahrain International Circuit").country("Bahrain").city("Sakhir").flagEmoji("🇧🇭").raceUTC("2026-04-12T15:00:00Z").qualifyingUTC("2026-04-11T16:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Charles Leclerc").winningConstructor("Ferrari").build(),
      RaceScheduleDTO.builder().round(5).raceName("Saudi Arabian Grand Prix").circuitName("Jeddah Corniche Circuit").country("Saudi Arabia").city("Jeddah").flagEmoji("🇸🇦").raceUTC("2026-04-19T17:00:00Z").qualifyingUTC("2026-04-18T17:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Lando Norris").winningConstructor("McLaren").build(),
      RaceScheduleDTO.builder().round(6).raceName("Miami Grand Prix").circuitName("Miami International Autodrome").country("United States").city("Miami").flagEmoji("🇺🇸").raceUTC("2026-05-03T20:00:00Z").qualifyingUTC("2026-05-02T20:00:00Z").hasSprint(true).status("COMPLETED").winnerName("Lando Norris").winningConstructor("McLaren").build(),
      RaceScheduleDTO.builder().round(7).raceName("Emilia Romagna Grand Prix").circuitName("Autodromo Enzo e Dino Ferrari").country("Italy").city("Imola").flagEmoji("🇮🇹").raceUTC("2026-05-17T13:00:00Z").qualifyingUTC("2026-05-16T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Max Verstappen").winningConstructor("Red Bull Racing").build(),
      RaceScheduleDTO.builder().round(8).raceName("Monaco Grand Prix").circuitName("Circuit de Monaco").country("Monaco").city("Monte Carlo").flagEmoji("🇲🇨").raceUTC("2026-05-24T13:00:00Z").qualifyingUTC("2026-05-23T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Charles Leclerc").winningConstructor("Ferrari").build(),
      RaceScheduleDTO.builder().round(9).raceName("Spanish Grand Prix").circuitName("Circuit de Barcelona-Catalunya").country("Spain").city("Barcelona").flagEmoji("🇪🇸").raceUTC("2026-06-07T13:00:00Z").qualifyingUTC("2026-06-06T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Max Verstappen").winningConstructor("Red Bull Racing").build(),
      RaceScheduleDTO.builder().round(10).raceName("Canadian Grand Prix").circuitName("Circuit Gilles-Villeneuve").country("Canada").city("Montreal").flagEmoji("🇨🇦").raceUTC("2026-06-14T18:00:00Z").qualifyingUTC("2026-06-13T20:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Max Verstappen").winningConstructor("Red Bull Racing").build(),
      RaceScheduleDTO.builder().round(11).raceName("Austrian Grand Prix").circuitName("Red Bull Ring").country("Austria").city("Spielberg").flagEmoji("🇦🇹").raceUTC("2026-06-28T13:00:00Z").qualifyingUTC("2026-06-27T14:00:00Z").hasSprint(true).status("COMPLETED").winnerName("George Russell").winningConstructor("Mercedes").build(),
      RaceScheduleDTO.builder().round(12).raceName("British Grand Prix").circuitName("Silverstone Circuit").country("United Kingdom").city("Silverstone").flagEmoji("🇬🇧").raceUTC("2026-07-05T14:00:00Z").qualifyingUTC("2026-07-04T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Lewis Hamilton").winningConstructor("Ferrari").build(),
      RaceScheduleDTO.builder().round(13).raceName("Hungarian Grand Prix").circuitName("Hungaroring").country("Hungary").city("Budapest").flagEmoji("🇭🇺").raceUTC("2026-07-19T13:00:00Z").qualifyingUTC("2026-07-18T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Oscar Piastri").winningConstructor("McLaren").build(),
      RaceScheduleDTO.builder().round(14).raceName("Belgian Grand Prix").circuitName("Circuit de Spa-Francorchamps").country("Belgium").city("Stavelot").flagEmoji("🇧🇪").raceUTC("2026-07-26T13:00:00Z").qualifyingUTC("2026-07-25T14:00:00Z").hasSprint(true).status("COMPLETED").winnerName("Lewis Hamilton").winningConstructor("Ferrari").build(),
      RaceScheduleDTO.builder().round(15).raceName("Dutch Grand Prix").circuitName("Circuit Zandvoort").country("Netherlands").city("Zandvoort").flagEmoji("🇳🇱").raceUTC("2026-08-23T13:00:00Z").qualifyingUTC("2026-08-22T13:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Lando Norris").winningConstructor("McLaren").build(),
      RaceScheduleDTO.builder().round(16).raceName("Italian Grand Prix").circuitName("Autodromo Nazionale Monza").country("Italy").city("Monza").flagEmoji("🇮🇹").raceUTC("2026-08-30T13:00:00Z").qualifyingUTC("2026-08-29T14:00:00Z").hasSprint(false).status("COMPLETED").winnerName("Charles Leclerc").winningConstructor("Ferrari").build(),
      RaceScheduleDTO.builder().round(17).raceName("Singapore Grand Prix").circuitName("Marina Bay Street Circuit").country("Singapore").city("Singapore").flagEmoji("🇸🇬").raceUTC("2026-10-04T12:00:00Z").qualifyingUTC("2026-10-03T13:00:00Z").hasSprint(false).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(18).raceName("United States Grand Prix").circuitName("Circuit of the Americas").country("United States").city("Austin").flagEmoji("🇺🇸").raceUTC("2026-10-18T19:00:00Z").qualifyingUTC("2026-10-17T21:00:00Z").hasSprint(true).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(19).raceName("Mexico City Grand Prix").circuitName("Autódromo Hermanos Rodríguez").country("Mexico").city("Mexico City").flagEmoji("🇲🇽").raceUTC("2026-10-25T20:00:00Z").qualifyingUTC("2026-10-24T20:00:00Z").hasSprint(false).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(20).raceName("São Paulo Grand Prix").circuitName("Autódromo José Carlos Pace").country("Brazil").city("São Paulo").flagEmoji("🇧🇷").raceUTC("2026-11-08T17:00:00Z").qualifyingUTC("2026-11-07T18:00:00Z").hasSprint(true).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(21).raceName("Las Vegas Grand Prix").circuitName("Las Vegas Strip Circuit").country("United States").city("Las Vegas").flagEmoji("🇺🇸").raceUTC("2026-11-21T06:00:00Z").qualifyingUTC("2026-11-20T06:00:00Z").hasSprint(false).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(22).raceName("Qatar Grand Prix").circuitName("Lusail International Circuit").country("Qatar").city("Lusail").flagEmoji("🇶🇦").raceUTC("2026-11-29T16:00:00Z").qualifyingUTC("2026-11-28T16:00:00Z").hasSprint(true).status("UPCOMING").build(),
      RaceScheduleDTO.builder().round(23).raceName("Abu Dhabi Grand Prix").circuitName("Yas Marina Circuit").country("United Arab Emirates").city("Abu Dhabi").flagEmoji("🇦🇪").raceUTC("2026-12-06T13:00:00Z").qualifyingUTC("2026-12-05T14:00:00Z").hasSprint(false).status("UPCOMING").build()
    );
  }

  private String resolveFlag(String country) {
    if (country == null) return "🏁";
    return switch (country.toLowerCase()) {
      case "australia" -> "🇦🇺";
      case "china" -> "🇨🇳";
      case "japan" -> "🇯🇵";
      case "bahrain" -> "🇧🇭";
      case "saudi arabia" -> "🇸🇦";
      case "united states", "usa" -> "🇺🇸";
      case "canada" -> "🇨🇦";
      case "monaco" -> "🇲🇨";
      case "spain" -> "🇪🇸";
      case "austria" -> "🇦🇹";
      case "united kingdom", "great britain" -> "🇬🇧";
      case "belgium" -> "🇧🇪";
      case "hungary" -> "🇭🇺";
      case "netherlands" -> "🇳🇱";
      case "italy" -> "🇮🇹";
      case "azerbaijan" -> "🇦🇿";
      case "singapore" -> "🇸🇬";
      case "mexico" -> "🇲🇽";
      case "brazil" -> "🇧🇷";
      case "qatar" -> "🇶🇦";
      case "united arab emirates", "uae" -> "🇦🇪";
      default -> "🏁";
    };
  }
}
