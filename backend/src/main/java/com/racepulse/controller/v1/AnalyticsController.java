package com.racepulse.controller.v1;

import com.racepulse.dto.AnalyticsDTO;
import com.racepulse.dto.ApiResponse;
import com.racepulse.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping
  public Mono<ApiResponse<AnalyticsDTO>> getFullAnalytics() {
    return analyticsService.getAnalytics()
      .map(data -> ApiResponse.ok("Motorsport analytics retrieved", data));
  }

  @GetMapping("/drivers")
  public Mono<ApiResponse<List<AnalyticsDTO.DriverConsistencyMetric>>> getDriverAnalytics() {
    return analyticsService.getAnalytics()
      .map(data -> ApiResponse.ok("Driver consistency analytics retrieved", data.getDriverConsistency()));
  }

  @GetMapping("/constructors")
  public Mono<ApiResponse<List<AnalyticsDTO.ConstructorPaceMetric>>> getConstructorAnalytics() {
    return analyticsService.getAnalytics()
      .map(data -> ApiResponse.ok("Constructor pace analytics retrieved", data.getConstructorPaceAnalysis()));
  }
}
