package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.DeviceType;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DeviceTypeMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDeviceTypeRequest(DeviceTypeRequest deviceTypeRequest, @MappingTarget DeviceType deviceType);

    DeviceTypeNameResponse toDeviceTypeNameResponse(DeviceType deviceType);

    DeviceType fromDeviceTypeRequest(DeviceTypeRequest deviceTypeRequest);

    DeviceTypeResponse toDeviceTypeResponse(DeviceType deviceType);

}
