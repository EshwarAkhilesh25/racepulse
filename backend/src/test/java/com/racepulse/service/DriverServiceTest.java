package com.racepulse.service;

import com.racepulse.dto.DriverDTO;
import com.racepulse.dto.DriverStatsDTO;
import com.racepulse.dto.StandingsDTO;
import com.racepulse.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DriverServiceTest {

  @Mock
  private StandingsService standingsService;

  private DriverService driverService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    driverService = new DriverService(standingsService);

    DriverDTO norris = DriverDTO.builder()
      .driverId("norris")
      .driverNumber(4)
      .code("NOR")
      .givenName("Lando")
      .familyName("Norris")
      .fullName("Lando Norris")
      .constructorId("mclaren")
      .constructorName("McLaren")
      .position(1)
      .points(331)
      .wins(7)
      .teamColor("#FF8000")
      .build();

    StandingsDTO standings = StandingsDTO.builder()
      .season(2026)
      .drivers(List.of(norris))
      .constructors(List.of())
      .build();

    when(standingsService.getStandings()).thenReturn(Mono.just(standings));
  }

  @Test
  void testGetDriverByIdSuccess() {
    StepVerifier.create(driverService.getDriverById("NOR"))
      .consumeNextWith(driver -> {
        assertEquals("NOR", driver.getCode());
        assertEquals("Lando Norris", driver.getFullName());
      })
      .verifyComplete();
  }

  @Test
  void testGetDriverByIdNotFound() {
    StepVerifier.create(driverService.getDriverById("NONEXISTENT"))
      .expectError(ResourceNotFoundException.class)
      .verify();
  }

  @Test
  void testGetDriverStatistics() {
    StepVerifier.create(driverService.getDriverStatistics("norris"))
      .consumeNextWith(stats -> {
        assertNotNull(stats);
        assertEquals("NOR", stats.getDriver().getCode());
        assertNotNull(stats.getTeammateComparison());
        assertEquals("piastri", stats.getTeammateComparison().getTeammateId());
        assertFalse(stats.getRecentFormPositions().isEmpty());
      })
      .verifyComplete();
  }
}
