package edu.npic.smartBuilding.features.analysis;

import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.analysis.dto.*;
import edu.npic.smartBuilding.features.building.BuildingRepository;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.floor.FloorRepository;
import edu.npic.smartBuilding.features.room.RoomRepository;
import edu.npic.smartBuilding.features.user.UserRepository;
import edu.npic.smartBuilding.util.AuthUtil;
import edu.npic.smartBuilding.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {


    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final AuthUtil authUtil;
    private final FloorRepository floorRepository;

    @Override
    public List<AnalysisRoomResponse> getAnalysisByRoomId(Integer roomId, LocalDate dateFrom, LocalDate dateTo) {

        List<Device> devices = deviceRepository.findByRoom_Id(roomId);
        List<AnalysisRoomResponse> analysisRoomResponses = devices.stream()
                .map(device -> {
                    List<Event> events = eventRepository.findByDevice_IdAndCreatedAtBetween(device.getId(), dateFrom.atStartOfDay(), dateTo.atStartOfDay());

//                    if (!device.getDeviceType().getControllable()) {
//                        Map<LocalDate, Double> dailySum = events.stream()
//                                .collect(Collectors.groupingBy(
//                                        event -> event.getCreatedAt().toLocalDate(),
//                                        Collectors.summingDouble(event -> Double.parseDouble(event.getValue()))
//                                ));
//
//                        List<Double> data = new ArrayList<>(dailySum.values());
//                        List<String> timestamps = dailySum.keySet().stream()
//                                .sorted()
//                                .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MMM/yy")))
//                                .toList();
//
//                        return AnalysisRoomResponse.builder()
//                                .deviceName(device.getName())
//                                .controllable(false)
//                                .series(List.of(
//                                        SeriesResponse.builder()
//                                                .data(data)
//                                                .build()
//                                ))
//                                .xAxis(timestamps)
//                                .build();
//                    }

                    List<Double> data = events.stream().map(e -> Double.valueOf(e.getValue())).collect(Collectors.toList());
                    List<String> timestamps = events.stream().map(e -> e.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MMM/yy HH:mm:ss"))).toList();

                    return AnalysisRoomResponse.builder()
                            .deviceName(device.getName())
                            .controllable(device.getDeviceType().getControllable())
                            .series(List.of(
                                    SeriesResponse.builder()
                                            .data(data)
                                            .build()
                            ))
                            .xAxis(timestamps)
                            .build();
                })
                .toList();

        return analysisRoomResponses;
    }


    @Override
    public AnalysisResponse getAnalysis(LocalDate dateFrom, LocalDate dateTo) {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        boolean isManager = authUtil.isManagerLoggedUser();
        if (dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date from must be before date to");
        }

        Long userCount = 0L;
        Long buildingCount = 0L;
        Long roomCount = 0L;
        Long deviceCount = 0L;
        List<SeriesResponse> series = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<String> labelsDevice = new ArrayList<>(List.of("Active", "Inactive"));
        List<Long> statusDeviceCount = new ArrayList<>();

        if (isManager) {
            buildingCount = buildingRepository.buildingCountByDate(dateFrom.atStartOfDay(), dateTo.atStartOfDay());
            userCount = userRepository.countUserByDate(dateFrom.atStartOfDay(), dateTo.atStartOfDay());
            roomCount = roomRepository.roomCountByDate(dateFrom.atStartOfDay(), dateTo.atStartOfDay());
            deviceCount = deviceRepository.countDeviceByDate(dateFrom.atStartOfDay(), dateTo.atStartOfDay());

            // Using String dates
            xAxis = DateUtil.calculateDuration(dateFrom, dateTo);
            series.add(SeriesResponse.builder()
                            .name("Power")
//                            .data(eventRepository.getValuePowerEventByDate(dateFrom.atStartOfDay(), dateTo.atStartOfDay()))
                    .build());
            statusDeviceCount.add(0, deviceRepository.countDeviceByStatus(DeviceStatus.Active));
            statusDeviceCount.add(1, deviceRepository.countDeviceByStatus(DeviceStatus.Inactive));
        }

        log.info("Series: " + series);
        return AnalysisResponse.builder()
                .userCount(userCount)
                .buildingCount(buildingCount)
                .roomCount(roomCount)
                .deviceCount(deviceCount)
                .powerAnalysis(PowerAnalysisResponse.builder()
                        .xAxis(xAxis)
                        .series(series)
                        .build())
                .statusDevice(StatusDeviceResponse.builder()
                        .labels(labelsDevice)
                        .series(statusDeviceCount)
                        .build())
                .build();
    }

    @Override
    public PowerAnalysisResponse getPowerAnalysis(String range) {
//        List<Building> buildings = buildingRepository.findAll();
//
//        LocalDate today = LocalDate.now();
//        LocalDateTime endDateTime = today.atTime(LocalTime.MAX);
//        LocalDate startDate;
//
//        switch (range) {
//            case "24h" -> startDate = today;
//            case "7d" -> startDate = today.minusDays(6);
//            case "28d" -> startDate = today.minusDays(27);
//            case "90d" -> startDate = today.minusDays(89);
//            case "365d" -> startDate = today.minusDays(364);
//            case "lifetime" -> {
//                Sort sortCreatedAt = Sort.by(Sort.Direction.ASC, "createdAt");
//                Event firstEvent = eventRepository.findAll(sortCreatedAt).getFirst();
//                startDate = firstEvent.getCreatedAt().toLocalDate();
//            }
//            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Range not match!");
//        }
//
//        List<SeriesResponse> series = new ArrayList<>();
//        double totalPower = 0.0;
//
//        for (Building building : buildings) {
//            if (range.equals("24h")) {
//                // Hourly analysis for current day
//                Map<Integer, Double> hourlyMap = new TreeMap<>();
//                for (int hour = 0; hour < 24; hour++) {
//                    hourlyMap.put(hour, 0.0);
//                }
//
//                List<Event> todayEvents = eventRepository.findPowerEventByBuildingId(
//                        startDate.atStartOfDay(), endDateTime, building.getId()
//                );
//
//                Map<Integer, Double> actualHourly = todayEvents.stream()
//                        .collect(Collectors.groupingBy(
//                                event -> event.getCreatedAt().getHour(),
//                                Collectors.summingDouble(e -> Double.parseDouble(e.getValue()))
//                        ));
//
//                actualHourly.forEach(hourlyMap::put);
//
//                List<DataResponse> data = hourlyMap.entrySet().stream().map(entry -> {
//                    LocalDateTime time = startDate.atTime(entry.getKey(), 0);
//                    long timestamp = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                    return DataResponse.builder()
//                            .x(timestamp)
//                            .y(entry.getValue())
//                            .build();
//                }).toList();
//
//                totalPower += hourlyMap.values().stream().mapToDouble(Double::doubleValue).sum();
//
//                series.add(SeriesResponse.builder()
//                        .name(building.getName())
//                        .data(data)
//                        .build());
//
//                continue;

//            }

//            // Daily analysis for 7d, 28d, etc.
//            Map<LocalDate, Double> dailyPowerMap = new TreeMap<>();
//            LocalDate fillDate = startDate;
//            while (!fillDate.isAfter(today)) {
//                dailyPowerMap.put(fillDate, 0.0);
//                fillDate = fillDate.plusDays(1);
//            }
//
//            List<Event> buildingEvents = eventRepository.findPowerEventByBuildingId(
//                    startDate.atStartOfDay(), endDateTime, building.getId()
//            );
//
//            Map<LocalDate, Double> actualData = buildingEvents.stream()
//                    .collect(Collectors.groupingBy(
//                            event -> event.getCreatedAt().toLocalDate(),
//                            Collectors.summingDouble(eventSum -> Double.parseDouble(eventSum.getValue()))
//                    ));
//
//            actualData.forEach(dailyPowerMap::put);
//
//            List<DataResponse> data = dailyPowerMap.entrySet().stream().map(entry -> {
//                long timestamp = entry.getKey()
//                        .atStartOfDay(ZoneId.systemDefault())
//                        .toInstant()
//                        .toEpochMilli();
//                return DataResponse.builder()
//                        .x(timestamp)
//                        .y(entry.getValue())
//                        .build();
//            }).toList();
//
//            totalPower += dailyPowerMap.values().stream().mapToDouble(Double::doubleValue).sum();
//
//            series.add(SeriesResponse.builder()
//                    .name(building.getName())
//                    .data(data)
//                    .build());
//        }
//
//        return PowerAnalysisResponse.builder()
//                .totalPower(String.valueOf(totalPower))
//                .series(series)
//                .build();
        return null;
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
