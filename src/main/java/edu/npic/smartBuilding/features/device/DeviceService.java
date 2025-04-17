package edu.npic.smartBuilding.features.device;

import edu.npic.smartBuilding.features.device.dto.DeviceRequest;
import edu.npic.smartBuilding.features.device.dto.DeviceResponse;
import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import edu.npic.smartBuilding.features.device.dto.DevicesRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeviceService {

    DeviceResponse getDeviceId(Integer id);

    DeviceResponse create(DeviceRequest deviceRequest);

    Page<DeviceResponse> findAll(int pageNo, int pageSize);

    DeviceResponse updateValue(Integer id, String value);

    List<DeviceResponse> createManyDevices(DevicesRequest devicesRequest);

    DeviceResponse updateSingleDevice(Integer id, DeviceRequest deviceRequest);

    void deleteDevice(Integer id);

    Page<DeviceResponse> filterDevice(int pageNo, int pageSize, String keywords, List<Integer> deviceTypeId, List<Integer> buildingId);
}
