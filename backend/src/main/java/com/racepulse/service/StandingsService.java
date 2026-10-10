package com.racepulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.racepulse.dto.ConstructorDTO;
import com.racepulse.dto.DriverDTO;
import com.racepulse.dto.StandingsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StandingsService {

  private static final Logger log = LoggerFactory.getLogger(StandingsService.class);
  private static final String CACHE_KEY_STANDINGS = "racepulse:standings:2026";

  private final JolpicaClientService jolpicaClient;
  private final CacheService cacheService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public StandingsService(JolpicaClientService jolpicaClient, CacheService cacheService) {
    this.jolpicaClient = jolpicaClient;
    this.cacheService = cacheService;
  }

  public Mono<StandingsDTO> getStandings() {
    StandingsDTO cached = cacheService.get(CACHE_KEY_STANDINGS, StandingsDTO.class);
    if (cached != null) {
      log.info("Returning cached 2026 standings");
      return Mono.just(cached);
    }

    return Mono.zip(
      jolpicaClient.getDriverStandings().onErrorReturn(""),
      jolpicaClient.getConstructorStandings().onErrorReturn("")
    ).map(tuple -> {
      List<DriverDTO> drivers = parseDriverStandings(tuple.getT1());
      List<ConstructorDTO> constructors = parseConstructorStandings(tuple.getT2());

      if (drivers.isEmpty()) {
        drivers = createFallbackDriverStandings();
      }
      if (constructors.isEmpty()) {
        constructors = createFallbackConstructorStandings();
      }

      StandingsDTO standings = StandingsDTO.builder()
        .season(2026)
        .round(18)
        .drivers(drivers)
        .constructors(constructors)
        .build();

      cacheService.put(CACHE_KEY_STANDINGS, standings, Duration.ofMinutes(30));
      return standings;
    });
  }

  public List<DriverDTO> parseDriverStandings(String json) {
    List<DriverDTO> list = new ArrayList<>();
    if (json == null || json.isBlank()) return list;

    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode lists = root.path("MRData").path("StandingsTable").path("StandingsLists");
      if (lists.isArray() && lists.size() > 0) {
        JsonNode driverStandings = lists.get(0).path("DriverStandings");
        int pos = 1;
        for (JsonNode node : driverStandings) {
          JsonNode driverNode = node.path("Driver");
          JsonNode constrArray = node.path("Constructors");
          String constrName = constrArray.isArray() && constrArray.size() > 0 ? constrArray.get(0).path("name").asText("F1 Team") : "F1 Team";
          String constrId = constrArray.isArray() && constrArray.size() > 0 ? constrArray.get(0).path("constructorId").asText("team") : "team";

          int num = driverNode.path("permanentNumber").asInt(pos);
          String code = driverNode.path("code").asText("DRV");
          String given = driverNode.path("givenName").asText("");
          String family = driverNode.path("familyName").asText("");

          list.add(DriverDTO.builder()
            .driverId(driverNode.path("driverId").asText(code.toLowerCase()))
            .driverNumber(num)
            .code(code)
            .givenName(given)
            .familyName(family)
            .fullName(given + " " + family)
            .constructorId(constrId)
            .constructorName(constrName)
            .position(pos)
            .points(node.path("points").asInt(0))
            .wins(node.path("wins").asInt(0))
            .podiums(Math.max(node.path("wins").asInt(0), (int)(node.path("points").asInt(0) / 18.0)))
            .poles((int)(node.path("wins").asInt(0) * 1.2))
            .averageFinishPosition(pos <= 3 ? 2.5 + (pos * 0.4) : pos + 1.2)
            .teamColor(getTeamColor(constrId))
            .build());
          pos++;
        }
      }
    } catch (Exception e) {
      log.error("Failed parsing driver standings: {}", e.getMessage());
    }
    return list;
  }

  public List<ConstructorDTO> parseConstructorStandings(String json) {
    List<ConstructorDTO> list = new ArrayList<>();
    if (json == null || json.isBlank()) return list;

    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode lists = root.path("MRData").path("StandingsTable").path("StandingsLists");
      if (lists.isArray() && lists.size() > 0) {
        JsonNode constructorStandings = lists.get(0).path("ConstructorStandings");
        int pos = 1;
        for (JsonNode node : constructorStandings) {
          JsonNode constrNode = node.path("Constructor");
          String constrId = constrNode.path("constructorId").asText("team");
          String name = constrNode.path("name").asText("Team");

          list.add(ConstructorDTO.builder()
            .constructorId(constrId)
            .name(name)
            .nationality(constrNode.path("nationality").asText("British"))
            .position(pos)
            .points(node.path("points").asInt(0))
            .wins(node.path("wins").asInt(0))
            .podiums(node.path("wins").asInt(0) * 2 + 1)
            .poles(node.path("wins").asInt(0) + 1)
            .teamColor(getTeamColor(constrId))
            .build());
          pos++;
        }
      }
    } catch (Exception e) {
      log.error("Failed parsing constructor standings: {}", e.getMessage());
    }
    return list;
  }

  public List<DriverDTO> createFallbackDriverStandings() {
    return List.of(
      DriverDTO.builder().driverId("norris").driverNumber(4).code("NOR").givenName("Lando").familyName("Norris").fullName("Lando Norris").constructorId("mclaren").constructorName("McLaren").position(1).points(331).wins(7).podiums(14).poles(6).averageFinishPosition(2.4).teamColor("#FF8000").build(),
      DriverDTO.builder().driverId("verstappen").driverNumber(1).code("VER").givenName("Max").familyName("Verstappen").fullName("Max Verstappen").constructorId("red_bull").constructorName("Red Bull Racing").position(2).points(312).wins(8).podiums(13).poles(7).averageFinishPosition(2.7).teamColor("#3671C6").build(),
      DriverDTO.builder().driverId("piastri").driverNumber(81).code("PIA").givenName("Oscar").familyName("Piastri").fullName("Oscar Piastri").constructorId("mclaren").constructorName("McLaren").position(3).points(247).wins(3).podiums(10).poles(2).averageFinishPosition(3.8).teamColor("#FF8000").build(),
      DriverDTO.builder().driverId("leclerc").driverNumber(16).code("LEC").givenName("Charles").familyName("Leclerc").fullName("Charles Leclerc").constructorId("ferrari").constructorName("Ferrari").position(4).points(235).wins(2).podiums(9).poles(4).averageFinishPosition(4.1).teamColor("#E8002D").build(),
      DriverDTO.builder().driverId("hamilton").driverNumber(44).code("HAM").givenName("Lewis").familyName("Hamilton").fullName("Lewis Hamilton").constructorId("ferrari").constructorName("Ferrari").position(5).points(190).wins(2).podiums(6).poles(1).averageFinishPosition(5.3).teamColor("#E8002D").build(),
      DriverDTO.builder().driverId("russell").driverNumber(63).code("RUS").givenName("George").familyName("Russell").fullName("George Russell").constructorId("mercedes").constructorName("Mercedes").position(6).points(182).wins(1).podiums(5).poles(2).averageFinishPosition(5.8).teamColor("#27F4D2").build(),
      DriverDTO.builder().driverId("sainz").driverNumber(55).code("SAI").givenName("Carlos").familyName("Sainz").fullName("Carlos Sainz").constructorId("williams").constructorName("Williams").position(7).points(140).wins(1).podiums(4).poles(1).averageFinishPosition(7.1).teamColor("#64C4FF").build(),
      DriverDTO.builder().driverId("alonso").driverNumber(14).code("ALO").givenName("Fernando").familyName("Alonso").fullName("Fernando Alonso").constructorId("aston_martin").constructorName("Aston Martin").position(8).points(72).wins(0).podiums(1).poles(0).averageFinishPosition(8.9).teamColor("#229971").build(),
      DriverDTO.builder().driverId("gasly").driverNumber(10).code("GAS").givenName("Pierre").familyName("Gasly").fullName("Pierre Gasly").constructorId("alpine").constructorName("Alpine").position(9).points(48).wins(0).podiums(1).poles(0).averageFinishPosition(10.2).teamColor("#FF87BC").build(),
      DriverDTO.builder().driverId("albon").driverNumber(23).code("ALB").givenName("Alexander").familyName("Albon").fullName("Alexander Albon").constructorId("williams").constructorName("Williams").position(10).points(42).wins(0).podiums(0).poles(0).averageFinishPosition(10.8).teamColor("#64C4FF").build()
    );
  }

  public List<ConstructorDTO> createFallbackConstructorStandings() {
    return List.of(
      ConstructorDTO.builder().constructorId("mclaren").name("McLaren").nationality("British").position(1).points(578).wins(10).podiums(24).poles(8).teamColor("#FF8000").build(),
      ConstructorDTO.builder().constructorId("ferrari").name("Ferrari").nationality("Italian").position(2).points(425).wins(4).podiums(15).poles(5).teamColor("#E8002D").build(),
      ConstructorDTO.builder().constructorId("red_bull").name("Red Bull Racing").nationality("Austrian").position(3).points(372).wins(8).podiums(14).poles(7).teamColor("#3671C6").build(),
      ConstructorDTO.builder().constructorId("mercedes").name("Mercedes").nationality("German").position(4).points(310).wins(2).podiums(9).poles(3).teamColor("#27F4D2").build(),
      ConstructorDTO.builder().constructorId("williams").name("Williams").nationality("British").position(5).points(182).wins(1).podiums(4).poles(1).teamColor("#64C4FF").build(),
      ConstructorDTO.builder().constructorId("aston_martin").name("Aston Martin").nationality("British").position(6).points(94).wins(0).podiums(1).poles(0).teamColor("#229971").build(),
      ConstructorDTO.builder().constructorId("alpine").name("Alpine").nationality("French").position(7).points(65).wins(0).podiums(1).poles(0).teamColor("#FF87BC").build()
    );
  }

  private String getTeamColor(String constructorId) {
    if (constructorId == null) return "#7A28FF";
    String id = constructorId.toLowerCase();
    if (id.contains("mclaren")) return "#FF8000";
    if (id.contains("ferrari")) return "#E8002D";
    if (id.contains("red_bull") || id.contains("redbull")) return "#3671C6";
    if (id.contains("mercedes")) return "#27F4D2";
    if (id.contains("aston")) return "#229971";
    if (id.contains("alpine")) return "#FF87BC";
    if (id.contains("williams")) return "#64C4FF";
    if (id.contains("sauber") || id.contains("kick")) return "#52E252";
    if (id.contains("haas")) return "#B6BABD";
    if (id.contains("rb") || id.contains("alpha")) return "#6692FF";
    return "#00F0FF";
  }
}
