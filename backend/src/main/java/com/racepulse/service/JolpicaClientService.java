package com.racepulse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class JolpicaClientService {

  private static final Logger log = LoggerFactory.getLogger(JolpicaClientService.class);
  private final WebClient webClient;

  public JolpicaClientService(WebClient.Builder builder,
                             @Value("${jolpica.base.url:https://api.jolpi.ca/ergast/f1}") String baseUrl) {
    this.webClient = builder.baseUrl(baseUrl).build();
  }

  public Mono<String> getDriverStandings() {
    return webClient.get()
      .uri("/current/driverStandings.json")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(15))
      .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
      .doOnError(e -> log.error("Jolpica Driver Standings error: {}", e.getMessage()));
  }

  public Mono<String> getConstructorStandings() {
    return webClient.get()
      .uri("/current/constructorStandings.json")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(15))
      .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
      .doOnError(e -> log.error("Jolpica Constructor Standings error: {}", e.getMessage()));
  }

  public Mono<String> getCurrentSchedule() {
    return webClient.get()
      .uri("/current.json")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(15))
      .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
      .doOnError(e -> log.error("Jolpica Schedule error: {}", e.getMessage()));
  }

  public Mono<String> getRecentResults() {
    return webClient.get()
      .uri("/current/last/results.json")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(15))
      .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
      .doOnError(e -> log.error("Jolpica Last Results error: {}", e.getMessage()));
  }

  public Mono<String> getHistoricalResults(int season) {
    return webClient.get()
      .uri("/" + season + "/results.json?limit=100")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(15))
      .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))
      .doOnError(e -> log.error("Jolpica Historical Results error for {}: {}", season, e.getMessage()));
  }
}
