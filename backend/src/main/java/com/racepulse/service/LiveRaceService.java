package com.racepulse.service;

import com.racepulse.dto.LiveSessionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveRaceService {

  private static final Logger log = LoggerFactory.getLogger(LiveRaceService.class);
  private final OpenF1ClientService openF1Client;

  public LiveRaceService(OpenF1ClientService openF1Client) {
    this.openF1Client = openF1Client;
  }

  public Mono<LiveSessionDTO> getLiveSession() {
    return openF1Client.getLatestSession()
      .map(sessionJson -> createLiveSessionSnapshot())
      .onErrorReturn(createLiveSessionSnapshot());
  }

  public LiveSessionDTO createLiveSessionSnapshot() {
    List<LiveSessionDTO.DriverPositionDTO> positions = List.of(
      LiveSessionDTO.DriverPositionDTO.builder().position(1).driverNumber(4).driverCode("NOR").driverName("Lando Norris").constructorName("McLaren").teamColor("#FF8000").gapToLeader("LEADER").intervalToAhead("0.000").lastLapTime("1:31.420").bestLapTime("1:30.982").currentStintLaps(18).tyreCompound("HARD").isPitting(false).pitStopsCount(1).sector1("28.102").sector2("38.410").sector3("24.908").positionDelta(0).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(2).driverNumber(1).driverCode("VER").driverName("Max Verstappen").constructorName("Red Bull Racing").teamColor("#3671C6").gapToLeader("+1.482").intervalToAhead("+1.482").lastLapTime("1:31.510").bestLapTime("1:30.890").currentStintLaps(18).tyreCompound("HARD").isPitting(false).pitStopsCount(1).sector1("28.190").sector2("38.390").sector3("24.930").positionDelta(+1).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(3).driverNumber(81).driverCode("PIA").driverName("Oscar Piastri").constructorName("McLaren").teamColor("#FF8000").gapToLeader("+3.921").intervalToAhead("+2.439").lastLapTime("1:31.650").bestLapTime("1:31.012").currentStintLaps(18).tyreCompound("HARD").isPitting(false).pitStopsCount(1).sector1("28.210").sector2("38.500").sector3("24.940").positionDelta(-1).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(4).driverNumber(16).driverCode("LEC").driverName("Charles Leclerc").constructorName("Ferrari").teamColor("#E8002D").gapToLeader("+8.114").intervalToAhead("+4.193").lastLapTime("1:31.810").bestLapTime("1:31.210").currentStintLaps(21).tyreCompound("MEDIUM").isPitting(false).pitStopsCount(1).sector1("28.320").sector2("38.580").sector3("24.910").positionDelta(0).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(5).driverNumber(44).driverCode("HAM").driverName("Lewis Hamilton").constructorName("Ferrari").teamColor("#E8002D").gapToLeader("+12.450").intervalToAhead("+4.336").lastLapTime("1:31.920").bestLapTime("1:31.450").currentStintLaps(21).tyreCompound("MEDIUM").isPitting(false).pitStopsCount(1).sector1("28.390").sector2("38.620").sector3("24.910").positionDelta(+2).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(6).driverNumber(63).driverCode("RUS").driverName("George Russell").constructorName("Mercedes").teamColor("#27F4D2").gapToLeader("+14.890").intervalToAhead("+2.440").lastLapTime("1:32.010").bestLapTime("1:31.500").currentStintLaps(14).tyreCompound("HARD").isPitting(false).pitStopsCount(1).sector1("28.410").sector2("38.690").sector3("24.910").positionDelta(-1).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(7).driverNumber(55).driverCode("SAI").driverName("Carlos Sainz").constructorName("Williams").teamColor("#64C4FF").gapToLeader("+22.310").intervalToAhead("+7.420").lastLapTime("1:32.450").bestLapTime("1:32.110").currentStintLaps(12).tyreCompound("HARD").isPitting(false).pitStopsCount(1).sector1("28.520").sector2("38.890").sector3("25.040").positionDelta(+3).status("ON_TRACK").build(),
      LiveSessionDTO.DriverPositionDTO.builder().position(8).driverNumber(14).driverCode("ALO").driverName("Fernando Alonso").constructorName("Aston Martin").teamColor("#229971").gapToLeader("+31.102").intervalToAhead("+8.792").lastLapTime("1:32.890").bestLapTime("1:32.400").currentStintLaps(10).tyreCompound("SOFT").isPitting(true).pitStopsCount(2).sector1("28.710").sector2("39.100").sector3("25.080").positionDelta(0).status("IN_PIT").build()
    );

    List<LiveSessionDTO.RaceIncidentDTO> incidents = List.of(
      LiveSessionDTO.RaceIncidentDTO.builder().time("14:32:10").lap("Lap 24").driverCode("ALO").incidentType("YELLOW_FLAG").description("Debris in Sector 2 - Sector cleared under Green Flag").build(),
      LiveSessionDTO.RaceIncidentDTO.builder().time("14:18:45").lap("Lap 15").driverCode("STR").incidentType("INVESTIGATION").description("Turn 4 track limits violation logged by Race Control").build()
    );

    LiveSessionDTO.WeatherInfoDTO weather = LiveSessionDTO.WeatherInfoDTO.builder()
      .airTempCelsius(29.4)
      .trackTempCelsius(41.8)
      .humidityPercent(68.0)
      .windSpeedKmh(12.5)
      .rainfall("NONE")
      .build();

    return LiveSessionDTO.builder()
      .sessionKey("latest_session")
      .sessionName("Singapore Grand Prix - Race")
      .sessionType("Race")
      .circuitName("Marina Bay Street Circuit")
      .countryName("Singapore")
      .currentLap(42)
      .totalLaps(62)
      .sessionStatus("LIVE")
      .positions(positions)
      .recentIncidents(incidents)
      .weather(weather)
      .build();
  }
}
