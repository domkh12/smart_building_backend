package edu.npic.smartBuilding.features.deviceType;

import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.domain.DeviceType;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeNameResponse;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeRequest;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import edu.npic.smartBuilding.mapper.DeviceTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceTypeServiceImpl implements DeviceTypeService{

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceTypeMapper deviceTypeMapper;

    @Override
    public List<DeviceTypeNameResponse> deviceTypeNames() {
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll();
        return deviceTypes.stream().map(deviceTypeMapper::toDeviceTypeNameResponse).toList();
    }

    @Override
    public DeviceTypeResponse create(DeviceTypeRequest deviceTypeRequest) {

        if(deviceTypeRepository.existsByName(deviceTypeRequest.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Device type already exists");
        }

        DeviceType deviceType = deviceTypeMapper.fromDeviceTypeRequest(deviceTypeRequest);
        deviceType.setUuid(UUID.randomUUID().toString());
        deviceType.setCreatedAt(LocalDateTime.now());
        deviceTypeRepository.save(deviceType);
        return deviceTypeMapper.toDeviceTypeResponse(deviceType);
    }

    @Override
    public List<DeviceTypeResponse> findAll() {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll(sort);

        return deviceTypes.stream().map(deviceTypeMapper::toDeviceTypeResponse).toList();
    }
}
