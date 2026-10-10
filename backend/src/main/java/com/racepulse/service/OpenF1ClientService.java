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
public class OpenF1ClientService {

  private static final Logger log = LoggerFactory.getLogger(OpenF1ClientService.class);
  private final WebClient webClient;

  public OpenF1ClientService(WebClient.Builder builder,
                             @Value("${openf1.base.url:https://api.openf1.org/v1}") String baseUrl) {
    this.webClient = builder.baseUrl(baseUrl).build();
  }

  public Mono<String> getLivePositionData() {
    return webClient.get()
      .uri("/position?session_key=latest")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Live Position error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }

  public Mono<String> getLatestSession() {
    return webClient.get()
      .uri("/sessions?session_key=latest")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Latest Session error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }

  public Mono<String> getDriversForSession(String sessionKey) {
    return webClient.get()
      .uri("/drivers?session_key=" + sessionKey)
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Drivers error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }

  public Mono<String> getLapData(String sessionKey) {
    return webClient.get()
      .uri("/laps?session_key=" + sessionKey)
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Lap Data error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }

  public Mono<String> getStintData(String sessionKey) {
    return webClient.get()
      .uri("/stints?session_key=" + sessionKey)
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Stints error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }

  public Mono<String> getIntervalData() {
    return webClient.get()
      .uri("/intervals?session_key=latest")
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(500)))
      .doOnError(e -> log.error("OpenF1 API Intervals error: {}", e.getMessage()))
      .onErrorReturn("[]");
  }
}
