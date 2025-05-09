package edu.npic.smartBuilding.features.analysis;

import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.analysis.dto.DataResponse;
import edu.npic.smartBuilding.features.analysis.dto.PowerAnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.SeriesResponse;
import edu.npic.smartBuilding.features.analysis.dto.TotalCountResponse;
import edu.npic.smartBuilding.features.building.BuildingRepository;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.floor.FloorRepository;
import edu.npic.smartBuilding.features.room.RoomRepository;
import edu.npic.smartBuilding.features.user.UserRepository;
import edu.npic.smartBuilding.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {


    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final AuthUtil authUtil;
    private final FloorRepository floorRepository;

    @Override
    public PowerAnalysisResponse getPowerAnalysis(String range) {
        List<Building> buildings = buildingRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDateTime endDateTime = today.atTime(LocalTime.MAX);
        LocalDate startDate;

        switch (range) {
            case "24h" -> startDate = today;
            case "7d" -> startDate = today.minusDays(6);
            case "28d" -> startDate = today.minusDays(27);
            case "90d" -> startDate = today.minusDays(89);
            case "365d" -> startDate = today.minusDays(364);
            case "lifetime" -> {
                Sort sortCreatedAt = Sort.by(Sort.Direction.ASC, "createdAt");
                Event firstEvent = eventRepository.findAll(sortCreatedAt).getFirst();
                startDate = firstEvent.getCreatedAt().toLocalDate();
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Range not match!");
        }

        List<SeriesResponse> series = new ArrayList<>();
        double totalPower = 0.0;

        for (Building building : buildings) {
            if (range.equals("24h")) {
                // Hourly analysis for current day
                Map<Integer, Double> hourlyMap = new TreeMap<>();
                for (int hour = 0; hour < 24; hour++) {
                    hourlyMap.put(hour, 0.0);
                }

                List<Event> todayEvents = eventRepository.findPowerEventByBuildingId(
                        startDate.atStartOfDay(), endDateTime, building.getId()
                );

                Map<Integer, Double> actualHourly = todayEvents.stream()
                        .collect(Collectors.groupingBy(
                                event -> event.getCreatedAt().getHour(),
                                Collectors.summingDouble(e -> Double.parseDouble(e.getValue()))
                        ));

                actualHourly.forEach(hourlyMap::put);

                List<DataResponse> data = hourlyMap.entrySet().stream().map(entry -> {
                    LocalDateTime time = startDate.atTime(entry.getKey(), 0);
                    long timestamp = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    return DataResponse.builder()
                            .x(timestamp)
                            .y(entry.getValue())
                            .build();
                }).toList();

                totalPower += hourlyMap.values().stream().mapToDouble(Double::doubleValue).sum();

                series.add(SeriesResponse.builder()
                        .name(building.getName())
                        .data(data)
                        .build());

                continue;
            }

            // Daily analysis for 7d, 28d, etc.
            Map<LocalDate, Double> dailyPowerMap = new TreeMap<>();
            LocalDate fillDate = startDate;
            while (!fillDate.isAfter(today)) {
                dailyPowerMap.put(fillDate, 0.0);
                fillDate = fillDate.plusDays(1);
            }

            List<Event> buildingEvents = eventRepository.findPowerEventByBuildingId(
                    startDate.atStartOfDay(), endDateTime, building.getId()
            );

            Map<LocalDate, Double> actualData = buildingEvents.stream()
                    .collect(Collectors.groupingBy(
                            event -> event.getCreatedAt().toLocalDate(),
                            Collectors.summingDouble(eventSum -> Double.parseDouble(eventSum.getValue()))
                    ));

            actualData.forEach(dailyPowerMap::put);

            List<DataResponse> data = dailyPowerMap.entrySet().stream().map(entry -> {
                long timestamp = entry.getKey()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                return DataResponse.builder()
                        .x(timestamp)
                        .y(entry.getValue())
                        .build();
            }).toList();

            totalPower += dailyPowerMap.values().stream().mapToDouble(Double::doubleValue).sum();

            series.add(SeriesResponse.builder()
                    .name(building.getName())
                    .data(data)
                    .build());
        }

        return PowerAnalysisResponse.builder()
                .totalPower(String.valueOf(totalPower))
                .series(series)
                .build();
    }



    @Override
    public TotalCountResponse getTotalCounts() {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();
        System.out.println("roomIds: " + roomIds);
        long countUsers = 0;
        long countDevices = 0;
        long countBuildings;
        long countRooms = 0;
        long countFloors = 0;

        if (isAdmin) {
            for (Long roomId : roomIds) {
                System.out.println("roomId: " + roomId);
                countUsers += userRepository.countUserByRoomId(roomId.intValue());
                countRooms += roomRepository.countRoomByRoomId(roomId.intValue());
                countDevices += deviceRepository.countByRoom_Id(roomId.intValue());
                countFloors += floorRepository.countByRooms_Id(roomId.intValue());
                return TotalCountResponse.builder().totalRoomCount(countRooms).totalFloorCount(countFloors).totalDeviceCount(countDevices).totalUserCount(countUsers).build();
            }
        } else {
            System.out.println("Not admin logged in");
            countUsers = userRepository.count();
            countDevices = deviceRepository.count();
            countBuildings = buildingRepository.count();
            countRooms = roomRepository.count();
            return TotalCountResponse.builder().totalRoomCount(countRooms).totalBuildingCount(countBuildings).totalDeviceCount(countDevices).totalUserCount(countUsers).build();
        }
        return null;
    }
}
