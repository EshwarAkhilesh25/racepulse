package com.racepulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceScheduleDTO {
  private int round;
  private String raceName;
  private String circuitName;
  private String country;
  private String city;
  private String flagEmoji;
  private String raceUTC;
  private String qualifyingUTC;
  private String fp1UTC;
  private String fp2UTC;
  private String fp3UTC;
  private String sprintUTC;
  private boolean hasSprint;
  private String status; // UPCOMING, LIVE, COMPLETED
  private String winnerName;
  private String winningConstructor;
}
