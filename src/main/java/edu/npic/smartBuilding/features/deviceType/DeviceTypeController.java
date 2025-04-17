package edu.npic.smartBuilding.features.deviceType;

import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/names")
    List<DeviceTypeNameResponse> deviceTypeNames() {
        return deviceTypeService.deviceTypeNames();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    DeviceTypeResponse create(@RequestBody DeviceTypeRequest deviceTypeRequest) {
        return deviceTypeService.create(deviceTypeRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<DeviceTypeResponse> findAll(){
        return deviceTypeService.findAll();
    }

}
