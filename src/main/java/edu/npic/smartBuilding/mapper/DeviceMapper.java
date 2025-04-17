package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.features.device.dto.DeviceRequest;
import edu.npic.smartBuilding.features.device.dto.DeviceResponse;
import edu.npic.smartBuilding.features.hardware.dto.DeviceResponseHardware;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeviceMapper {



    Device fromDeviceRequest(DeviceRequest deviceRequest);

    void updateFromDeviceRequest(DeviceRequest deviceRequest, @MappingTarget Device device);

    DeviceResponseHardware todeviceResponseHardware(Device device);

    DeviceResponse toDeviceResponse(Device device);

}
