package edu.npic.smartBuilding.features.device;

import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.domain.*;
import edu.npic.smartBuilding.features.building.BuildingRepository;
import edu.npic.smartBuilding.features.device.dto.DeviceRequest;
import edu.npic.smartBuilding.features.device.dto.DeviceResponse;
import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import edu.npic.smartBuilding.features.device.dto.DevicesRequest;
import edu.npic.smartBuilding.features.deviceType.DeviceTypeRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.room.RoomRepository;
import edu.npic.smartBuilding.mapper.DeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService{

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final DeviceTypeRepository deviceTypeRepository;
    private final EventRepository eventRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    @Override
    public DeviceResponse getDeviceId(Integer id) {
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")
        );
        return deviceMapper.toDeviceResponse(device);
    }

    @Override
    public Page<DeviceResponse> filterDevice(int pageNo, int pageSize, String keywords, List<Integer> deviceTypeId, List<Integer> buildingId) {

        if(pageNo < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than zero");
        }else if(pageSize < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be greater than zero");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);

        List<Integer> deviceTypeIds = null;
        if (!deviceTypeId.isEmpty()){
            deviceTypeIds = deviceTypeId;
        }

        List<Integer> buildingIds = null;
        if (!buildingId.isEmpty()) {
            buildingIds = buildingId;
        }

        Page<Device> devices = deviceRepository.filterDevice(keywords, deviceTypeIds, buildingIds, pageRequest);

        return devices.map(deviceMapper::toDeviceResponse);
    }

    @Override
    public void deleteDevice(Integer id) {
        Room room = roomRepository.findByDevices_Id(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This device does not exist in room!")
        );
        room.setDevicesQty(room.getDevicesQty() - 1);
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")
        );

        deviceRepository.delete(device);
        roomRepository.save(room);
    }

    @Override
    public DeviceResponse updateSingleDevice(Integer id, DeviceRequest deviceRequest) {
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")
        );

        deviceMapper.updateFromDeviceRequest(deviceRequest, device);
        DeviceType deviceType = deviceTypeRepository.findById(deviceRequest.deviceTypeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device type not found")
        );
        device.setDeviceType(deviceType);
        Device savedDevice = deviceRepository.save(device);
        return deviceMapper.toDeviceResponse(savedDevice);
    }

    @Override
    public List<DeviceResponse> createManyDevices(DevicesRequest devicesRequest) {

      List<Device> devices = devicesRequest.devices().stream().map(device -> {
            Device device1 = deviceMapper.fromDeviceRequest(device);
            DeviceType deviceType = deviceTypeRepository.findById(device.deviceTypeId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DeviceType not found!")
            );
            Room room = roomRepository.findById(device.roomId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
            );

            device1.setUuid(UUID.randomUUID().toString());
            device1.setDeviceType(deviceType);
            device1.setCreatedAt(LocalDateTime.now());
            device1.setRoom(room);
            device1.setEvents(new ArrayList<>());
            device1.setStatus(DeviceStatus.Inactive);
            // increase device qty in room
            room.setDevicesQty(room.getDevicesQty() + 1);
            roomRepository.save(room);
          return deviceRepository.save(device1);
        }).toList();

        return devices.stream().map(deviceMapper::toDeviceResponse).toList();
    }

    @Override
    public DeviceResponse updateValue(Integer id, String value) {
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Event event = eventRepository.findByDevice_Uuid(device.getUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!")
        );

        event.setValue(value);
        eventRepository.save(event);

        return deviceMapper.toDeviceResponse(device);
    }

    @Override
    public Page<DeviceResponse> findAll(int pageNo, int pageSize) {
        if (pageNo <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be more than 0");
        }
        if (pageSize <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size must be more than 0");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1 , pageSize, sort);

        Page<Device> devicePage = deviceRepository.findAll(pageRequest);

        List<DeviceResponse> deviceResponses = devicePage.getContent().stream()
                .map(device -> {
                    List<Event> events = device.getEvents();
                    if (events != null) {
                        List<Event> limitedEvents = events.stream()
                                .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                                .limit(10)
                                .collect(Collectors.toList());
                        device.setEvents(limitedEvents);
                    }
                    return deviceMapper.toDeviceResponse(device);
                })
                .toList();

        return new PageImpl<>(deviceResponses, pageRequest, devicePage.getTotalElements()); // Return PageImpl with DeviceResponses
    }

    @Override
    public DeviceResponse create(DeviceRequest deviceRequest) {

//        Room room = roomRepository.findByUuid(deviceRequest.roomUuid()).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
//        );
//
//        DeviceType deviceType = deviceTypeRepository.findByUuid(deviceRequest.deviceTypeUuid()).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device type not found!")
//        );
//
//        Device device = deviceMapper.fromDeviceRequest(deviceRequest);
//        device.setStatus("inActive");
//        device.setUuid(UUID.randomUUID().toString());
//        device.setRoom(room);
//        device.setCreatedAt(LocalDateTime.now());
//        device.setDeviceType(deviceType);
//        deviceRepository.save(device);
//
//        room.setDevicesQty(room.getDevicesQty() + 1);
//        roomRepository.save(room);
//
//        return deviceMapper.toDeviceResponse(device);\
        return null;
    }
}
