package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.RaceScheduleDTO;
import com.racepulse.exception.ResourceNotFoundException;
import com.racepulse.service.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/races")
public class RaceController {

  private final ScheduleService scheduleService;

  public RaceController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  @GetMapping
  public Mono<ApiResponse<List<RaceScheduleDTO>>> getRaces() {
    return scheduleService.getSchedule()
      .map(races -> ApiResponse.ok("Race calendar retrieved", races));
  }

  @GetMapping("/{id}")
  public Mono<ApiResponse<RaceScheduleDTO>> getRaceById(@PathVariable String id) {
    return scheduleService.getSchedule()
      .map(races -> races.stream()
        .filter(r -> String.valueOf(r.getRound()).equals(id) || r.getRaceName().equalsIgnoreCase(id))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Race not found with identifier: " + id)))
      .map(race -> ApiResponse.ok("Race details retrieved", race));
  }
}
