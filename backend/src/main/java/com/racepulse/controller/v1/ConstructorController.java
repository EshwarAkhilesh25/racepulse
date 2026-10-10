package com.racepulse.controller.v1;

import com.racepulse.dto.ApiResponse;
import com.racepulse.dto.ConstructorDTO;
import com.racepulse.dto.ConstructorStatsDTO;
import com.racepulse.service.ConstructorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/constructors")
public class ConstructorController {

  private final ConstructorService constructorService;

  public ConstructorController(ConstructorService constructorService) {
    this.constructorService = constructorService;
  }

  @GetMapping
  public Mono<ApiResponse<List<ConstructorDTO>>> getConstructors() {
    return constructorService.getAllConstructors()
      .map(constructors -> ApiResponse.ok("F1 constructors retrieved", constructors));
  }

  @GetMapping("/{id}")
  public Mono<ApiResponse<ConstructorDTO>> getConstructorById(@PathVariable String id) {
    return constructorService.getConstructorById(id)
      .map(constructor -> ApiResponse.ok("Constructor profile retrieved", constructor));
  }

  @GetMapping("/{id}/statistics")
  public Mono<ApiResponse<ConstructorStatsDTO>> getConstructorStatistics(@PathVariable String id) {
    return constructorService.getConstructorStatistics(id)
      .map(stats -> ApiResponse.ok("Constructor intelligence & stats retrieved", stats));
  }
}
