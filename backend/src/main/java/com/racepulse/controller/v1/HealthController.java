package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

  @GetMapping
  public ApiResponse<Map<String, Object>> getHealth() {
    Map<String, Object> health = new HashMap<>();
    health.put("status", "UP");
    health.put("service", "RacePulse Intelligence API");
    health.put("version", "1.0.0");
    health.put("uptime", System.currentTimeMillis());
    return ApiResponse.ok("System healthy", health);
  }
}
