package edu.npic.smartBuilding.features.hardware;

import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HardwareServiceImpl implements HardwareService{

    private final DeviceRepository deviceRepository;
    private final EventRepository eventRepository;

    @Override
    public List<DeviceResponseHardware> findDevicesByRoomId(Integer id) {
        List<Device> devices = deviceRepository.findByRoom_Id(id);
        return devices.stream().map(device -> DeviceResponseHardware.builder()
                .id(device.getId())
                .value(device.getEvents().stream().map(event -> event.getValue()).findFirst() .orElse("0"))
                .build()).toList();
    }

    @Override
    public void createEvent(int deviceId, String value) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")
        );
        Event event = new Event();
        event.setUuid(UUID.randomUUID().toString());
        event.setValue(value);
        event.setDevice(device);
        event.setCreatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }
}
