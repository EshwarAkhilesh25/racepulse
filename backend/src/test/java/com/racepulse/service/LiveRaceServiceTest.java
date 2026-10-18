package com.racepulse.service;

import com.racepulse.dto.LiveSessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LiveRaceServiceTest {

  @Mock
  private OpenF1ClientService openF1Client;

  @Mock
  private JolpicaClientService jolpicaClient;

  @Mock
  private CacheService cacheService;

  private LiveRaceService liveRaceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    liveRaceService = new LiveRaceService(openF1Client, jolpicaClient, cacheService);
  }

  @Test
  void testGetLiveSessionWithOpenF1PopulatedData() {
    when(cacheService.get(any(), eq(LiveSessionDTO.class))).thenReturn(null);

    String sessionJson = """
      [{
        "session_key": 11388,
        "session_name": "Race",
        "session_type": "Race",
        "circuit_short_name": "Singapore",
        "country_name": "Singapore",
        "date_start": "2026-10-11T12:00:00+00:00",
        "date_end": "2026-10-11T14:00:00+00:00"
      }]
      """;

    String positionsJson = """
      [
        {"session_key": 11388, "driver_number": 81, "position": 2, "date": "2026-10-11T13:00:00Z"},
        {"session_key": 11388, "driver_number": 4, "position": 1, "date": "2026-10-11T13:00:00Z"},
        {"session_key": 11388, "driver_number": 81, "position": 1, "date": "2026-10-11T13:40:00Z"},
        {"session_key": 11388, "driver_number": 4, "position": 2, "date": "2026-10-11T13:40:00Z"}
      ]
      """;

    String driversJson = """
      [
        {"session_key": 11388, "driver_number": 81, "broadcast_name": "O PIASTRI", "full_name": "Oscar Piastri", "name_acronym": "PIA", "team_name": "McLaren", "team_colour": "FF8000"},
        {"session_key": 11388, "driver_number": 4, "broadcast_name": "L NORRIS", "full_name": "Lando Norris", "name_acronym": "NOR", "team_name": "McLaren", "team_colour": "FF8000"}
      ]
      """;

    String stintsJson = """
      [
        {"session_key": 11388, "driver_number": 81, "compound": "HARD", "stint_number": 2, "lap_start": 20, "lap_end": 62},
        {"session_key": 11388, "driver_number": 4, "compound": "MEDIUM", "stint_number": 2, "lap_start": 21, "lap_end": 62}
      ]
      """;

    String weatherJson = """
      [
        {"session_key": 11388, "air_temperature": 29.5, "track_temperature": 39.8, "humidity": 65.0, "wind_speed": 3.2, "rainfall": 0}
      ]
      """;

    String rcJson = """
      [
        {"session_key": 11388, "category": "Flag", "flag": "CHEQUERED", "message": "CHEQUERED FLAG", "lap_number": 62, "date": "2026-10-11T13:45:00Z"}
      ]
      """;

    when(openF1Client.getLatestSession()).thenReturn(Mono.just(sessionJson));
    when(openF1Client.getLivePositionData("11388")).thenReturn(Mono.just(positionsJson));
    when(openF1Client.getDriversForSession("11388")).thenReturn(Mono.just(driversJson));
    when(openF1Client.getStintData("11388")).thenReturn(Mono.just(stintsJson));
    when(openF1Client.getWeather("11388")).thenReturn(Mono.just(weatherJson));
    when(openF1Client.getRaceControl("11388")).thenReturn(Mono.just(rcJson));

    StepVerifier.create(liveRaceService.getLiveSession())
      .consumeNextWith(session -> {
        assertNotNull(session);
        assertEquals("11388", session.getSessionKey());
        assertTrue(session.getSessionName().contains("Singapore"));
        assertEquals("COMPLETED", session.getSessionStatus()); // date_end was in the past
        assertEquals(2, session.getPositions().size());

        // Driver 81 was position 1 at 13:40:00Z
        LiveSessionDTO.DriverPositionDTO p1 = session.getPositions().get(0);
        assertEquals(1, p1.getPosition());
        assertEquals(81, p1.getDriverNumber());
        assertEquals("PIA", p1.getDriverCode());
        assertEquals("Oscar Piastri", p1.getDriverName());
        assertEquals("HARD", p1.getTyreCompound());
        assertEquals("#FF8000", p1.getTeamColor());

        // Driver 4 was position 2 at 13:40:00Z
        LiveSessionDTO.DriverPositionDTO p2 = session.getPositions().get(1);
        assertEquals(2, p2.getPosition());
        assertEquals(4, p2.getDriverNumber());
        assertEquals("NOR", p2.getDriverCode());
        assertEquals("MEDIUM", p2.getTyreCompound());

        // Weather
        assertNotNull(session.getWeather());
        assertEquals(29.5, session.getWeather().getAirTempCelsius());
        assertEquals(39.8, session.getWeather().getTrackTempCelsius());

        // Incidents
        assertFalse(session.getRecentIncidents().isEmpty());
        assertEquals("CHEQUERED FLAG", session.getRecentIncidents().get(0).getDescription());
      })
      .verifyComplete();
  }

  @Test
  void testGetLiveSessionPreservesMetadataWhenPositionsAreEmpty() {
    when(cacheService.get(any(), eq(LiveSessionDTO.class))).thenReturn(null);

    String sessionJson = """
      [{
        "session_key": 11390,
        "session_name": "Practice 1",
        "session_type": "Practice",
        "circuit_short_name": "Austin",
        "country_name": "United States",
        "date_start": "2026-10-23T17:30:00+00:00",
        "date_end": "2026-10-23T18:30:00+00:00"
      }]
      """;

    when(openF1Client.getLatestSession()).thenReturn(Mono.just(sessionJson));
    when(openF1Client.getLivePositionData("11390")).thenReturn(Mono.just("[]"));
    when(openF1Client.getDriversForSession("11390")).thenReturn(Mono.just("[]"));
    when(openF1Client.getStintData("11390")).thenReturn(Mono.just("[]"));
    when(openF1Client.getWeather("11390")).thenReturn(Mono.just("[]"));
    when(openF1Client.getRaceControl("11390")).thenReturn(Mono.just("[]"));

    StepVerifier.create(liveRaceService.getLiveSession())
      .consumeNextWith(session -> {
        assertNotNull(session);
        assertEquals("11390", session.getSessionKey());
        assertTrue(session.getSessionName().contains("Austin"));
        assertEquals("UPCOMING", session.getSessionStatus()); // future date
        assertTrue(session.getPositions().isEmpty(), "Positions should be empty without forcing fallback to Jolpica");
      })
      .verifyComplete();
  }

  @Test
  void testGetLiveSessionFallbacksToJolpicaWhenOpenF1Fails() {
    when(cacheService.get(any(), eq(LiveSessionDTO.class))).thenReturn(null);
    when(openF1Client.getLatestSession()).thenReturn(Mono.error(new RuntimeException("OpenF1 Down")));

    String jolpicaJson = """
      {
        "MRData": {
          "RaceTable": {
            "Races": [{
              "raceName": "Singapore Grand Prix",
              "Circuit": {
                "circuitName": "Marina Bay Street Circuit",
                "Location": {"country": "Singapore"}
              },
              "Results": [
                {
                  "number": "81",
                  "position": "1",
                  "status": "Finished",
                  "Driver": {"code": "PIA", "givenName": "Oscar", "familyName": "Piastri"},
                  "Constructor": {"constructorId": "mclaren", "name": "McLaren"},
                  "Time": {"time": "1:35:42.123"}
                }
              ]
            }]
          }
        }
      }
      """;

    when(jolpicaClient.getRecentResults()).thenReturn(Mono.just(jolpicaJson));

    StepVerifier.create(liveRaceService.getLiveSession())
      .consumeNextWith(session -> {
        assertNotNull(session);
        assertEquals("latest_classification", session.getSessionKey());
        assertTrue(session.getSessionName().contains("Singapore Grand Prix"));
        assertEquals("COMPLETED", session.getSessionStatus());
        assertEquals(1, session.getPositions().size());
        assertEquals("PIA", session.getPositions().get(0).getDriverCode());
      })
      .verifyComplete();
  }

  @Test
  void testGetLiveSessionOfflineFallbackWhenBothFail() {
    when(cacheService.get(any(), eq(LiveSessionDTO.class))).thenReturn(null);
    when(openF1Client.getLatestSession()).thenReturn(Mono.error(new RuntimeException("OpenF1 Down")));
    when(jolpicaClient.getRecentResults()).thenReturn(Mono.error(new RuntimeException("Jolpica Down")));

    StepVerifier.create(liveRaceService.getLiveSession())
      .consumeNextWith(session -> {
        assertNotNull(session);
        assertEquals("fallback_classification", session.getSessionKey());
        assertTrue(session.getSessionName().contains("Bahrain"));
        assertEquals("COMPLETED", session.getSessionStatus());
        assertFalse(session.getPositions().isEmpty());
      })
      .verifyComplete();
  }

  @Test
  void testLiveStatusDeterminationForActiveSession() {
    Instant now = Instant.now();
    String start = now.minus(30, ChronoUnit.MINUTES).toString();
    String end = now.plus(90, ChronoUnit.MINUTES).toString();

    String sessionJson = String.format("""
      [{
        "session_key": 9999,
        "session_name": "Grand Prix",
        "session_type": "Race",
        "circuit_short_name": "Silverstone",
        "country_name": "Great Britain",
        "date_start": "%s",
        "date_end": "%s"
      }]
      """, start, end);

    LiveRaceService.OpenF1SessionMeta meta = liveRaceService.parseOpenF1SessionMetadata(sessionJson);
    assertNotNull(meta);
    assertEquals("LIVE", meta.status);
  }
}
