package edu.npic.smartBuilding.features.device;

import edu.npic.smartBuilding.features.device.dto.DeviceRequest;
import edu.npic.smartBuilding.features.device.dto.DeviceResponse;
import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import edu.npic.smartBuilding.features.device.dto.DevicesRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    DeviceResponse getDeviceById(@PathVariable Integer id) {
        return deviceService.getDeviceId(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    Page<DeviceResponse> filterDevice(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "") String keywords,
            @RequestParam(required = false, defaultValue = "") List<Integer> deviceTypeId,
            @RequestParam(required = false, defaultValue = "") List<Integer> buildingId
    ){
        return deviceService.filterDevice(pageNo, pageSize, keywords, deviceTypeId, buildingId);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDevice(@PathVariable Integer id) {
        deviceService.deleteDevice(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    DeviceResponse updateSingleDevice(@PathVariable Integer id, @RequestBody @Valid DeviceRequest deviceRequest) {
        return deviceService.updateSingleDevice(id, deviceRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}/value")
    @ResponseStatus(HttpStatus.CREATED)
    DeviceResponse updateValue(@PathVariable Integer id, @RequestParam String value){
        return deviceService.updateValue(id, value);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    List<DeviceResponse> createManyDevices(@Valid @RequestBody DevicesRequest devicesRequest){
        return deviceService.createManyDevices(devicesRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<DeviceResponse> findAll(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ){
        return deviceService.findAll(pageNo, pageSize);
    }

}
