package edu.npic.smartBuilding.features.deviceType;

import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;

import java.util.List;

public interface DeviceTypeService {

    List<DeviceTypeResponse> findAll();

    DeviceTypeResponse create(DeviceTypeRequest deviceTypeRequest);

    List<DeviceTypeNameResponse> deviceTypeNames();
}
