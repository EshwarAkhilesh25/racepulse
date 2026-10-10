package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.StandingsDTO;
import com.racepulse.service.StandingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/standings")
public class StandingsController {

  private final StandingsService standingsService;

  public StandingsController(StandingsService standingsService) {
    this.standingsService = standingsService;
  }

  @GetMapping
  public Mono<ApiResponse<StandingsDTO>> getStandings() {
    return standingsService.getStandings()
      .map(standings -> ApiResponse.ok("Championship standings retrieved", standings));
  }
}
