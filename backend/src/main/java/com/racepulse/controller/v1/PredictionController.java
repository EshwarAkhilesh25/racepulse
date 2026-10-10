package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.PredictionDTO;
import com.racepulse.service.PredictionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/predictions")
public class PredictionController {

  private final PredictionService predictionService;

  public PredictionController(PredictionService predictionService) {
    this.predictionService = predictionService;
  }

  @GetMapping
  public Mono<ApiResponse<PredictionDTO>> getPredictions() {
    return predictionService.getPredictions()
      .map(data -> ApiResponse.ok("Statistical predictions & trend projections retrieved", data));
  }
}
