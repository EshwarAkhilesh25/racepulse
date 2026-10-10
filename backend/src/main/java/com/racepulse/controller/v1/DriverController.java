package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.DriverDTO;
import com.racepulse.dto.DriverStatsDTO;
import com.racepulse.service.DriverService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

  private final DriverService driverService;

  public DriverController(DriverService driverService) {
    this.driverService = driverService;
  }

  @GetMapping
  public Mono<ApiResponse<List<DriverDTO>>> getDrivers() {
    return driverService.getAllDrivers()
      .map(drivers -> ApiResponse.ok("F1 drivers retrieved", drivers));
  }

  @GetMapping("/{id}")
  public Mono<ApiResponse<DriverDTO>> getDriverById(@PathVariable String id) {
    return driverService.getDriverById(id)
      .map(driver -> ApiResponse.ok("Driver profile retrieved", driver));
  }

  @GetMapping("/{id}/statistics")
  public Mono<ApiResponse<DriverStatsDTO>> getDriverStatistics(@PathVariable String id) {
    return driverService.getDriverStatistics(id)
      .map(stats -> ApiResponse.ok("Driver statistics & intelligence retrieved", stats));
  }
}
