package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.DeviceType;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceTypeMapper {

    DeviceTypeNameResponse toDeviceTypeNameResponse(DeviceType deviceType);

    DeviceType fromDeviceTypeRequest(DeviceTypeRequest deviceTypeRequest);

    DeviceTypeResponse toDeviceTypeResponse(DeviceType deviceType);

}
