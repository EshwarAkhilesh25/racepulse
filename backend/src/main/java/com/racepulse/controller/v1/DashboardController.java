package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.DashboardDTO;
import com.racepulse.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping
  public Mono<ApiResponse<DashboardDTO>> getDashboard() {
    return dashboardService.getDashboardData()
      .map(data -> ApiResponse.ok("RacePulse intelligence dashboard retrieved", data));
  }
}
