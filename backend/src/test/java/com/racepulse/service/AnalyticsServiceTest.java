package com.racepulse.service;

import com.racepulse.dto.AnalyticsDTO;
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
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

  @Mock
  private StandingsService standingsService;

  private AnalyticsService analyticsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    analyticsService = new AnalyticsService(standingsService);

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
      .averageFinishPosition(2.4)
      .build();

    when(standingsService.getStandings()).thenReturn(Mono.just(StandingsDTO.builder()
      .drivers(List.of(norris))
      .constructors(List.of())
      .build()));
  }

  @Test
  void testGetAnalytics() {
    StepVerifier.create(analyticsService.getAnalytics())
      .consumeNextWith(analytics -> {
        assertNotNull(analytics);
        assertFalse(analytics.getDriverConsistency().isEmpty());
        assertEquals("NOR", analytics.getDriverConsistency().get(0).getDriverCode());
        assertFalse(analytics.getQualiVsRacePerformance().isEmpty());
        assertFalse(analytics.getPositionGainLoss().isEmpty());
        assertFalse(analytics.getConstructorPaceAnalysis().isEmpty());
      })
      .verifyComplete();
  }
}
