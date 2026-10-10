package com.racepulse.service;

import com.racepulse.dto.DriverDTO;
import com.racepulse.dto.StandingsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StandingsServiceTest {

  @Mock
  private JolpicaClientService jolpicaClient;

  @Mock
  private CacheService cacheService;

  private StandingsService standingsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    standingsService = new StandingsService(jolpicaClient, cacheService);
  }

  @Test
  void testGetStandingsFallbackWhenExternalApiFails() {
    when(cacheService.get(any(), any())).thenReturn(null);
    when(jolpicaClient.getDriverStandings()).thenReturn(Mono.error(new RuntimeException("API Down")));
    when(jolpicaClient.getConstructorStandings()).thenReturn(Mono.error(new RuntimeException("API Down")));

    Mono<StandingsDTO> result = standingsService.getStandings();

    StepVerifier.create(result)
      .consumeNextWith(standings -> {
        assertNotNull(standings);
        assertFalse(standings.getDrivers().isEmpty());
        assertFalse(standings.getConstructors().isEmpty());
        assertEquals("Lando Norris", standings.getDrivers().get(0).getFullName());
        assertEquals("McLaren", standings.getConstructors().get(0).getName());
      })
      .verifyComplete();
  }

  @Test
  void testParseDriverStandings() {
    String mockJson = """
      {
        "MRData": {
          "StandingsTable": {
            "StandingsLists": [{
              "DriverStandings": [{
                "position": "1",
                "points": "331",
                "wins": "7",
                "Driver": {
                  "driverId": "norris",
                  "permanentNumber": "4",
                  "code": "NOR",
                  "givenName": "Lando",
                  "familyName": "Norris"
                },
                "Constructors": [{"constructorId": "mclaren", "name": "McLaren"}]
              }]
            }]
          }
        }
      }
      """;

    List<DriverDTO> drivers = standingsService.parseDriverStandings(mockJson);

    assertEquals(1, drivers.size());
    DriverDTO d = drivers.get(0);
    assertEquals("NOR", d.getCode());
    assertEquals("Lando Norris", d.getFullName());
    assertEquals(331, d.getPoints());
    assertEquals(7, d.getWins());
    assertEquals("#FF8000", d.getTeamColor());
  }
}
