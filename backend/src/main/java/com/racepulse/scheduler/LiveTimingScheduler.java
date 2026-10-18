package com.racepulse.scheduler;

import com.racepulse.service.LiveRaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LiveTimingScheduler {

  private static final Logger log = LoggerFactory.getLogger(LiveTimingScheduler.class);

  private final LiveRaceService liveRaceService;
  private final SimpMessagingTemplate messagingTemplate;

  public LiveTimingScheduler(LiveRaceService liveRaceService, SimpMessagingTemplate messagingTemplate) {
    this.liveRaceService = liveRaceService;
    this.messagingTemplate = messagingTemplate;
  }

  @Scheduled(fixedDelayString = "${timing.poll.interval:2000}")
  public void pollAndBroadcast() {
    liveRaceService.getLiveSession()
      .timeout(Duration.ofSeconds(5))
      .subscribe(
        snapshot -> {
          messagingTemplate.convertAndSend("/topic/timing", snapshot);
          messagingTemplate.convertAndSend("/topic/live", snapshot);
          log.debug("Broadcasted live race telemetry to WebSocket subscribers");
        },
        error -> log.error("Failed broadcasting websocket telemetry: {}", error.getMessage())
      );
  }
}
