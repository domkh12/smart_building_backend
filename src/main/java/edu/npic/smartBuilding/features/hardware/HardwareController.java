package edu.npic.smartBuilding.features.hardware;

import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hardware")
@RequiredArgsConstructor
public class HardwareController {
    private final HardwareService hardwareService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    void createEvent(@RequestParam int deviceId, @RequestParam String value){
        hardwareService.createEvent(deviceId, value);
    }

    @GetMapping("/devices/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    List<DeviceResponseHardware> findDevicesByRoomId(@PathVariable Integer id){
        return hardwareService.findDevicesByRoomId(id);
    }
}
