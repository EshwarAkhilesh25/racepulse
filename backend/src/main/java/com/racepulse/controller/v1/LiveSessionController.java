package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.LiveSessionDTO;
import com.racepulse.service.LiveRaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/live")
public class LiveSessionController {

  private final LiveRaceService liveRaceService;

  public LiveSessionController(LiveRaceService liveRaceService) {
    this.liveRaceService = liveRaceService;
  }

  @GetMapping("/session")
  public Mono<ApiResponse<LiveSessionDTO>> getLiveSession() {
    return liveRaceService.getLiveSession()
      .map(session -> ApiResponse.ok("Live session telemetry retrieved", session));
  }
}
