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
import edu.npic.smartBuilding.util.AuthUtil;
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
    private final AuthUtil authUtil;

    @Override
    public DeviceResponse getDeviceId(Integer id) {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        boolean isManager = authUtil.isManagerLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();

        Device device = new Device();
        if (isManager){
            device = deviceRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")
            );
        }else if (isAdmin){
            device = deviceRepository.findDeviceByIdAndRoomIds(id, roomIds).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to view this device")
            );
        }

        return deviceMapper.toDeviceResponse(device);
    }

    @Override
    public Page<DeviceResponse> filterDevice(int pageNo, int pageSize, String keywords, List<Integer> deviceTypeId, List<Integer> buildingId) {
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();

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

        Page<Device> devices = Page.empty();

        if (isManager){
            devices = deviceRepository.filterDevice(keywords, deviceTypeIds, buildingIds, pageRequest);
        }else if (isAdmin){
            devices = deviceRepository.filterDeviceRoleAdmin(keywords, deviceTypeIds, buildingIds, roomIds, pageRequest);
        }

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
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();

        if (pageNo <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be more than 0");
        }
        if (pageSize <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size must be more than 0");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1 , pageSize, sort);

        Page<Device> devicePage = Page.empty();

        if (isManager){
            devicePage = deviceRepository.findAll(pageRequest);
        }else if (isAdmin){
            devicePage = deviceRepository.findDeviceByRoomIds(roomIds, pageRequest);
        }

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

        return new PageImpl<>(deviceResponses, pageRequest, devicePage.getTotalElements());
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
